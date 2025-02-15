// 创建Vue应用
const { createApp, ref, onMounted } = Vue;
const { ElMessage, ElMessageBox } = ElementPlus;

const app = createApp({
    setup() {
        // 状态变量
        const activeMenu = ref('rules');
        const rules = ref([]);
        const templates = ref([]);
        const executionLogs = ref([]);
        const executionStats = ref({});
        const testResult = ref(null);

        // 规则对话框
        const ruleDialog = ref({
            visible: false,
            type: 'create',
            form: {
                code: '',
                name: '',
                description: '',
                condition: '',
                action: '',
                priority: 1
            }
        });

        // 模板对话框
        const templateDialog = ref({
            visible: false,
            type: 'create',
            form: {
                code: '',
                name: '',
                description: '',
                conditionTemplate: '',
                actionTemplate: '',
                parameterDefinitions: '[]'
            }
        });

        // 从模板创建规则对话框
        const createFromTemplateDialog = ref({
            visible: false,
            template: null,
            form: {
                code: '',
                name: '',
                parameters: {}
            }
        });

        // 版本历史对话框
        const versionDialog = ref({
            visible: false,
            versions: []
        });

        // 导出表单
        const exportForm = ref({
            ruleIds: [],
            format: 'excel'
        });

        // 测试表单
        const testForm = ref({
            ruleId: '',
            params: ''
        });

        // 加载规则列表
        const loadRules = async () => {
            try {
                const response = await fetch('/api/rules');
                rules.value = await response.json();
            } catch (error) {
                ElMessage.error('加载规则列表失败');
                console.error('加载规则列表失败:', error);
            }
        };

        // 加载模板列表
        const loadTemplates = async () => {
            try {
                const response = await fetch('/api/rule-templates');
                templates.value = await response.json();
            } catch (error) {
                ElMessage.error('加载模板列表失败');
                console.error('加载模板列表失败:', error);
            }
        };

        // 加载执行日志
        const loadExecutionLogs = async () => {
            try {
                const response = await fetch('/api/execution-logs');
                executionLogs.value = await response.json();
            } catch (error) {
                ElMessage.error('加载执行日志失败');
                console.error('加载执行日志失败:', error);
            }
        };

        // 加载执行统计
        const loadExecutionStats = async () => {
            try {
                const response = await fetch('/api/execution-stats');
                executionStats.value = await response.json();
            } catch (error) {
                ElMessage.error('加载执行统计失败');
                console.error('加载执行统计失败:', error);
            }
        };

        // 菜单选择处理
        const handleMenuSelect = (index) => {
            activeMenu.value = index;
            if (index === 'rules') {
                loadRules();
            } else if (index === 'templates') {
                loadTemplates();
            } else if (index === 'monitoring') {
                loadExecutionLogs();
                loadExecutionStats();
            }
        };

        // 显示创建规则对话框
        const showCreateRuleDialog = () => {
            ruleDialog.value.type = 'create';
            ruleDialog.value.form = {
                code: '',
                name: '',
                description: '',
                condition: '',
                action: '',
                priority: 1
            };
            ruleDialog.value.visible = true;
        };

        // 显示编辑规则对话框
        const editRule = (rule) => {
            ruleDialog.value.type = 'edit';
            ruleDialog.value.form = { ...rule };
            ruleDialog.value.visible = true;
        };

        // 保存规则
        const saveRule = async () => {
            try {
                const url = ruleDialog.value.type === 'create' ? '/api/rules' : `/api/rules/${ruleDialog.value.form.id}`;
                const method = ruleDialog.value.type === 'create' ? 'POST' : 'PUT';
                const response = await fetch(url, {
                    method,
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(ruleDialog.value.form)
                });

                if (!response.ok) {
                    throw new Error('保存规则失败');
                }

                ElMessage.success('保存规则成功');
                ruleDialog.value.visible = false;
                loadRules();
            } catch (error) {
                ElMessage.error(error.message);
                console.error('保存规则失败:', error);
            }
        };

        // 切换规则状态
        const toggleRuleStatus = async (rule) => {
            try {
                const newStatus = rule.status === 'ENABLED' ? 'DISABLED' : 'ENABLED';
                const response = await fetch(`/api/rules/${rule.id}/status`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ status: newStatus })
                });

                if (!response.ok) {
                    throw new Error('切换规则状态失败');
                }

                ElMessage.success('切换规则状态成功');
                loadRules();
            } catch (error) {
                ElMessage.error(error.message);
                console.error('切换规则状态失败:', error);
            }
        };

        // 删除规则
        const deleteRule = async (rule) => {
            try {
                await ElMessageBox.confirm('确定要删除该规则吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });

                const response = await fetch(`/api/rules/${rule.id}`, {
                    method: 'DELETE'
                });

                if (!response.ok) {
                    throw new Error('删除规则失败');
                }

                ElMessage.success('删除规则成功');
                loadRules();
            } catch (error) {
                if (error !== 'cancel') {
                    ElMessage.error(error.message || '删除规则失败');
                    console.error('删除规则失败:', error);
                }
            }
        };

        // 显示版本历史
        const showVersionHistory = async (rule) => {
            try {
                const response = await fetch(`/api/rule-versions/${rule.id}`);
                versionDialog.value.versions = await response.json();
                versionDialog.value.visible = true;
            } catch (error) {
                ElMessage.error('加载版本历史失败');
                console.error('加载版本历史失败:', error);
            }
        };

        // 回滚到指定版本
        const rollbackToVersion = async (version) => {
            try {
                await ElMessageBox.confirm('确定要回滚到该版本吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });

                const response = await fetch(`/api/rule-versions/${version.id}/rollback`, {
                    method: 'POST'
                });

                if (!response.ok) {
                    throw new Error('回滚版本失败');
                }

                ElMessage.success('回滚版本成功');
                versionDialog.value.visible = false;
                loadRules();
            } catch (error) {
                if (error !== 'cancel') {
                    ElMessage.error(error.message || '回滚版本失败');
                    console.error('回滚版本失败:', error);
                }
            }
        };

        // 显示创建模板对话框
        const showCreateTemplateDialog = () => {
            templateDialog.value.type = 'create';
            templateDialog.value.form = {
                code: '',
                name: '',
                description: '',
                conditionTemplate: '',
                actionTemplate: '',
                parameterDefinitions: '[]'
            };
            templateDialog.value.visible = true;
        };

        // 显示编辑模板对话框
        const editTemplate = (template) => {
            templateDialog.value.type = 'edit';
            templateDialog.value.form = { ...template };
            templateDialog.value.visible = true;
        };

        // 保存模板
        const saveTemplate = async () => {
            try {
                const url = templateDialog.value.type === 'create' ? '/api/rule-templates' : `/api/rule-templates/${templateDialog.value.form.id}`;
                const method = templateDialog.value.type === 'create' ? 'POST' : 'PUT';
                const response = await fetch(url, {
                    method,
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(templateDialog.value.form)
                });

                if (!response.ok) {
                    throw new Error('保存模板失败');
                }

                ElMessage.success('保存模板成功');
                templateDialog.value.visible = false;
                loadTemplates();
            } catch (error) {
                ElMessage.error(error.message);
                console.error('保存模板失败:', error);
            }
        };

        // 删除模板
        const deleteTemplate = async (template) => {
            try {
                await ElMessageBox.confirm('确定要删除该模板吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });

                const response = await fetch(`/api/rule-templates/${template.id}`, {
                    method: 'DELETE'
                });

                if (!response.ok) {
                    throw new Error('删除模板失败');
                }

                ElMessage.success('删除模板成功');
                loadTemplates();
            } catch (error) {
                if (error !== 'cancel') {
                    ElMessage.error(error.message || '删除模板失败');
                    console.error('删除模板失败:', error);
                }
            }
        };

        // 显示从模板创建规则对话框
        const showCreateFromTemplateDialog = (template) => {
            createFromTemplateDialog.value.template = template;
            createFromTemplateDialog.value.form = {
                code: '',
                name: '',
                parameters: {}
            };
            createFromTemplateDialog.value.visible = true;
        };

        // 从模板创建规则
        const saveRuleFromTemplate = async () => {
            try {
                const response = await fetch(`/api/rule-templates/${createFromTemplateDialog.value.template.id}/create-rule`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(createFromTemplateDialog.value.form)
                });

                if (!response.ok) {
                    throw new Error('从模板创建规则失败');
                }

                ElMessage.success('从模板创建规则成功');
                createFromTemplateDialog.value.visible = false;
                loadRules();
            } catch (error) {
                ElMessage.error(error.message);
                console.error('从模板创建规则失败:', error);
            }
        };

        // 导出规则
        const exportRules = async () => {
            if (!exportForm.value.ruleIds.length) {
                ElMessage.warning('请选择要导出的规则');
                return;
            }

            const format = exportForm.value.format;
            const ruleIds = exportForm.value.ruleIds.join(',');
            window.location.href = `/api/rule-import-export/export/${format}?ruleIds=${ruleIds}`;
        };

        // 执行规则测试
        const executeRule = async () => {
            if (!testForm.value.ruleId) {
                ElMessage.warning('请选择要测试的规则');
                return;
            }

            try {
                let params = {};
                try {
                    params = JSON.parse(testForm.value.params);
                } catch (e) {
                    ElMessage.error('参数格式不正确，请输入有效的JSON格式');
                    return;
                }

                const response = await fetch(`/api/rules/${testForm.value.ruleId}/test`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(params)
                });

                const result = await response.json();
                if (!response.ok) {
                    throw new Error(result.message || '规则测试失败');
                }

                testResult.value = result;
            } catch (error) {
                ElMessage.error(error.message);
                console.error('规则测试失败:', error);
            }
        };

        // 清空测试结果
        const clearTestResult = () => {
            testResult.value = null;
        };

        // 添加参数
        const addParameter = () => {
            const parameters = JSON.parse(templateDialog.value.form.parameterDefinitions);
            parameters.push({
                name: '',
                type: 'string',
                description: '',
                required: false
            });
            templateDialog.value.form.parameterDefinitions = JSON.stringify(parameters);
        };

        // 删除参数
        const removeParameter = (index) => {
            const parameters = JSON.parse(templateDialog.value.form.parameterDefinitions);
            parameters.splice(index, 1);
            templateDialog.value.form.parameterDefinitions = JSON.stringify(parameters);
        };

        // 页面加载时初始化数据
        onMounted(() => {
            loadRules();
        });

        return {
            activeMenu,
            rules,
            templates,
            executionLogs,
            executionStats,
            testResult,
            ruleDialog,
            templateDialog,
            createFromTemplateDialog,
            versionDialog,
            exportForm,
            testForm,
            handleMenuSelect,
            showCreateRuleDialog,
            editRule,
            saveRule,
            toggleRuleStatus,
            deleteRule,
            showVersionHistory,
            rollbackToVersion,
            showCreateTemplateDialog,
            editTemplate,
            saveTemplate,
            deleteTemplate,
            showCreateFromTemplateDialog,
            saveRuleFromTemplate,
            exportRules,
            executeRule,
            clearTestResult,
            addParameter,
            removeParameter
        };
    }
});

// 注册所有图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

// 挂载应用
app.use(ElementPlus);
app.mount('#app'); 