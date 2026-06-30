package top.mddata;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.pool.DruidDataSource;
import top.mddata.base.base.ExtraParams;
import top.mddata.base.base.entity.SuperEntity;
import top.mddata.base.base.entity.TreeEntity;
import top.mddata.base.mvcflex.controller.SuperController;
import top.mddata.base.mvcflex.mapper.SuperMapper;
import top.mddata.base.mvcflex.service.SuperService;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.codegen.Generator;
import top.mddata.codegen.config.GlobalConfig;
import top.mddata.codegen.constant.GenerationStrategyEnum;
import top.mddata.codegen.dialect.JdbcTypeMapping;
import top.mddata.codegen.entity.Column;
import top.mddata.codegen.entity.Table;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;

public class ConsoleGeneratorTest {

    public static void main(String[] args) {
        //配置数据源
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mdp?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

        //创建配置内容，两种风格都可以。
//        GlobalConfig globalConfig = createOpenBackend();
//        GlobalConfig globalConfig = createWorkbenchBackend();
//        GlobalConfig globalConfig = createConsoleBackend();
        GlobalConfig globalConfig = createConsoleFront();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();

    }

    public static GlobalConfig createConsoleFront() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        // TODO 是否树形结构
        boolean isTree = false;
        // 设置根包和生成的表名
        //globalConfig.setBasePackage("com.fsfsfs.demo.test");
//        globalConfig.setFrontSourceDir("/Users/tangyh/gitee/mdp-vben/apps/web-workbench");
        globalConfig.setFrontSourceDir("/Users/tangyh/gitee/mdp-vben/apps/web-console");
        globalConfig.setBasePackage("top.mddata.console");
//        globalConfig.getPackageConfig().setSubSystem("workbench")
//                .setModule("user");
        globalConfig.getPackageConfig().setSubSystem("console")
//                .setModule("message");
//                .setModule("system");
//                .setModule("permission");
//                .setModule("organization");
//        globalConfig.getPackageConfig().setSubSystem("open")
                .setModule("system");
//        globalConfig.setGenerateTable("mdc_resource_menu");

//        globalConfig.setGenerateTable("mdo_api",
//                "mdo_app",
//                "mdo_app_apply",
//                "mdo_app_keys",
//                "mdo_doc_group",
//                "mdo_doc_info",
//                "mdo_oauth_scope",
//                "mdo_scope_group",
//
//                "mdo_doc_content",
//                "mdo_group_api_rel",
//                "mdo_group_scope_rel"
//
////                ,"mdo_app_group_rel", "mdo_oauth_log", "mdo_oauth_openid", "mdo_oauth_unionid"
//        );
//        globalConfig.setGenerateTable("mdc_config", "mdc_dict", "mdc_dict_item", "mdc_request_log");
//        globalConfig.setGenerateTable("sys_user");
//        globalConfig.setGenerateTable("mdc_org");
//        globalConfig.setGenerateTable("mdw_login_log");
//        globalConfig.setGenerateTable("mdc_org", "mdc_position", "mdc_user");
//        globalConfig.setGenerateTable(
//                "mdo_event_type"
//                "mdc_interface_config",
//                "mdc_interface_stat",
//                "mdc_interface_log",
//                "mdc_msg_template",
//                "mdc_msg_task",
//                "mdc_msg_task_recipient"
//                "mdc_notice"
//                "mdc_notice_recipient"
//        );

        globalConfig.setGenerateTable("mdc_request_log");
//        globalConfig.setGenerateTable("mdo_event_push", "mdo_notify_info",
//                "mdo_event_push_log", "mdo_notify_info_log", "mdo_event_trigger");

//        globalConfig.setEntityGenerateEnable();
        //设置表前缀和只生成哪些表
//        globalConfig.setTablePrefix("mdw_");
//        globalConfig.setTablePrefix("mdo_");
        globalConfig.setTablePrefix("mdc_");
        globalConfig.setJdkVersion(17);
        globalConfig.getJavadocConfig()
                .setAuthor("henhen6")
                .setColumnCommentFormat(comment -> StrUtil.replace(comment, "\n", "\n     * "))
                .setColumnSwaggerCommentFormat(comment -> StrUtil.replace(StrUtil.subBefore(StrUtil.subBefore(comment, "\r\n", false), "\n", false), "\"", "\\\""))
                .setTableCommentFormat(comment -> StrUtil.replace(comment, "\n", "\n     * "))
                .setTableSwaggerCommentFormat(comment -> StrUtil.replace(StrUtil.subBefore(StrUtil.subBefore(comment, "\r\n", false), "\n", false), "\"", "\\\""))
        ;

        globalConfig.enableController().setRequestMappingPrefix("/" + globalConfig.getPackageConfig().getModule());

        globalConfig.disableBackend();
        if (isTree) {
            globalConfig.enableFrontTree();
        } else {
            globalConfig.enableFrontTable();
        }
        return globalConfig;
    }

    public static GlobalConfig createConsoleBackend() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        // TODO 是否树形结构
        GenerationStrategyEnum entityGenerationStrategy = GenerationStrategyEnum.OVERWRITE;
        boolean isTree = false;
        Class<?> idClass = Long.class;
//        Class<?> entityClass = top.mddata.base.base.entity.BaseEntity.class;  // 3个字段
        Class<?> entityClass = top.mddata.base.base.entity.SuperEntity.class;   // 5个字段
        // 设置根包和生成的表名
        //globalConfig.setBasePackage("com.fsfsfs.demo.test");
        globalConfig.setSourceDir(System.getProperty("user.dir") + "/mdp-apps/md-console");
        globalConfig.setBasePackage("top.mddata.console");
//        globalConfig.getPackageConfig().setSubSystem("").setModule("permission");
        globalConfig.getPackageConfig().setSubSystem("").setModule("system");
//        globalConfig.getPackageConfig().setSubSystem("").setModule("organization");
//        globalConfig.getPackageConfig().setSubSystem("").setModule("system");

//        globalConfig.setGenerateTable("mdc_resource_menu", "mdc_resource_button", "mdc_resource_data_perm", "mdc_resource_field", "mdc_role");
//        globalConfig.setGenerateTable("mdc_role_resource_rel", "mdc_resource_uri");

//        globalConfig.setGenerateTable("mdc_user", "mdc_position");
//        globalConfig.setGenerateTable("mdc_org");
//        globalConfig.setGenerateTable("mdc_role_app_rel");
//        globalConfig.setGenerateTable("mdc_org_nature", "mdc_user_org_rel", "mdc_user_role_rel");
        globalConfig.setGenerateTable(
                "mdc_request_log",
                "mdc_request_log_detail"

        );

//        globalConfig.setGenerateTable("mdc_dict", "mdc_dict_item", "mdc_file", "mdc_file_part", "mdc_config", "mdc_request_log");

//        globalConfig.setEntityGenerateEnable();

        //设置表前缀和只生成哪些表
        globalConfig.setTablePrefix("mdc_");
        globalConfig.setJdkVersion(17);
        globalConfig.getJavadocConfig()
                .setAuthor("henhen6")
                .setColumnCommentFormat(comment -> StrUtil.replace(comment, "\n", "\n     * "))
                .setColumnSwaggerCommentFormat(comment -> StrUtil.replace(StrUtil.subBefore(StrUtil.subBefore(comment, "\r\n", false), "\n", false), "\"", "\\\""))
                .setTableCommentFormat(comment -> StrUtil.replace(comment, "\n", "\n     * "))
                .setTableSwaggerCommentFormat(comment -> StrUtil.replace(StrUtil.subBefore(StrUtil.subBefore(comment, "\r\n", false), "\n", false), "\"", "\\\""))
        ;

//        globalConfig.setTableConfig(TableConfig.create()
//                .setInsertListenerClass(DefaultInsertListener.class)
//                .setUpdateListenerClass(DefaultUpdateListener.class));


        globalConfig.enableEntity()
                .setSourceDir(globalConfig.getSourceDir() + "/console-pojo")
                .setGenericityType(idClass)
                .setWithLombok(true)
                .setWithBaseClassEnable(true).setGenerationStrategy(entityGenerationStrategy)
        ;

        if (isTree) {
            globalConfig.getEntityConfig().setSuperClass(TreeEntity.class);
        } else {
            globalConfig.getEntityConfig().setSuperClass(entityClass);
//            globalConfig.getEntityConfig().setSuperClass(SuperEntity.class);
        }

        globalConfig.enableVo()
                .setSourceDir(globalConfig.getSourceDir() + "/console-pojo")
                .setWithLombok(true)
                .setWithSwagger(true)
                .setWithExcel(true)
                .setGenerationStrategy(entityGenerationStrategy)
//                .setImplInterfaces(Serializable.class)
        ;

        if (isTree) {
            globalConfig.getVoConfig()
                    .setSuperClass(TreeEntity.class)
                    .setGenericityType(idClass).setGenerationStrategy(entityGenerationStrategy);
        }
        globalConfig.enableQuery()
                .setSourceDir(globalConfig.getSourceDir() + "/console-pojo")
                .setSuperClass(ExtraParams.class)
                .setGenerationStrategy(entityGenerationStrategy)
                .setWithLombok(true).setWithSwagger(true).setWithExcel(true)
                .setImplInterfaces(Serializable.class);

//
        globalConfig.enableDto()
                .setSourceDir(globalConfig.getSourceDir() + "/console-pojo")
                .setWithLombok(true).setWithSwagger(true).setWithValidator(true)
                .setGenerationStrategy(entityGenerationStrategy)
                //.setImplInterfaces(Serializable.class)
                .setIgnoreColumns(new HashSet<>(Arrays.asList(SuperEntity.CREATED_AT_FIELD, SuperEntity.CREATED_BY_FIELD,
                        SuperEntity.UPDATED_AT_FIELD, SuperEntity.UPDATED_BY_FIELD,
                        SuperEntity.DELETED_AT_FIELD, SuperEntity.DELETED_BY_FIELD)));

        globalConfig.enableController()
                .setSourceDir(globalConfig.getSourceDir() + "/console-web")
                .setRequestMappingPrefix("/" + globalConfig.getPackageConfig().getModule())
                .setWithCrud(true)
//                .setSuperClass(SuperController.class)
//                .setSuperClass(SuperWriteController.class)
//                .setSuperClass(SuperReadController.class)
//               .setSuperClass(SuperSimpleController.class)
                .setGenerationStrategy(GenerationStrategyEnum.OVERWRITE);
//                .setGenerationStrategy(GenerationStrategyEnum.EXIST_IGNORE);

        globalConfig.getControllerConfig().setSuperClass(SuperController.class);

        globalConfig.enableMapperXml();

//        //设置生成 mapper
        globalConfig.enableMapper().setSourceDir(globalConfig.getSourceDir() + "/console-dao").setSuperClass(SuperMapper.class).setGenerationStrategy(GenerationStrategyEnum.EXIST_IGNORE);
        globalConfig.enableService().setSourceDir(globalConfig.getSourceDir() + "/console-service").setSuperClass(SuperService.class).setGenerationStrategy(GenerationStrategyEnum.EXIST_IGNORE);
        globalConfig.enableServiceImpl().setSourceDir(globalConfig.getSourceDir() + "/console-service").setSuperClass(SuperServiceImpl.class).setGenerationStrategy(GenerationStrategyEnum.EXIST_IGNORE);
//        globalConfig.enableFront();
        globalConfig.disableFrontTable();

        //可以单独配置某个列
//        ColumnConfig columnConfig = new ColumnConfig();
//        columnConfig.setColumnName("tenant_id");
//        columnConfig.setLarge(true);
//        columnConfig.setVersion(true);
//        globalConfig.setColumnConfig("sys_param", columnConfig);

        JdbcTypeMapping.setTypeMapper(new JdbcTypeMapping.JdbcTypeMapper() {
            @Override
            public String getType(String rawType, String jdbcType, Table table, Column column) {

                return null;
            }
        });


        return globalConfig;
    }

    public static GlobalConfig createWorkbenchBackend() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        // TODO 是否树形结构
        GenerationStrategyEnum entityGenerationStrategy = GenerationStrategyEnum.OVERWRITE;
        boolean isTree = false;
        Class<?> idClass = Long.class;
//        Class<?> entityClass = top.mddata.base.base.entity.BaseEntity.class;  // 3个字段
        Class<?> entityClass = top.mddata.base.base.entity.SuperEntity.class;   // 5个字段
        // 设置根包和生成的表名
        //globalConfig.setBasePackage("com.fsfsfs.demo.test");
        globalConfig.setSourceDir(System.getProperty("user.dir") + "/mdp-apps/md-workbench");
        globalConfig.setBasePackage("top.mddata.workbench");
//        globalConfig.getPackageConfig().setSubSystem("").setModule("permission");
//        globalConfig.getPackageConfig().setSubSystem("").setModule("organization");
        globalConfig.getPackageConfig().setSubSystem("").setModule("");

//        globalConfig.setGenerateTable("mdc_notice");
        globalConfig.setGenerateTable("mdc_notice_recipient");
//        globalConfig.setGenerateTable("mdw_login_log");

//        globalConfig.setEntityGenerateEnable();

        //设置表前缀和只生成哪些表
        globalConfig.setTablePrefix("mdc_");
//        globalConfig.setTablePrefix("mdw_");
        globalConfig.setJdkVersion(17);
        globalConfig.getJavadocConfig()
                .setAuthor("henhen6")
                .setColumnCommentFormat(comment -> StrUtil.replace(comment, "\n", "\n     * "))
                .setColumnSwaggerCommentFormat(comment -> StrUtil.replace(StrUtil.subBefore(StrUtil.subBefore(comment, "\r\n", false), "\n", false), "\"", "\\\""))
                .setTableCommentFormat(comment -> StrUtil.replace(comment, "\n", "\n     * "))
                .setTableSwaggerCommentFormat(comment -> StrUtil.replace(StrUtil.subBefore(StrUtil.subBefore(comment, "\r\n", false), "\n", false), "\"", "\\\""))
        ;

//        globalConfig.setTableConfig(TableConfig.create()
//                .setInsertListenerClass(DefaultInsertListener.class)
//                .setUpdateListenerClass(DefaultUpdateListener.class));


        globalConfig.enableEntity()
                .setSourceDir(globalConfig.getSourceDir() + "/workbench-pojo")
                .setGenericityType(idClass)
                .setWithLombok(true)
                .setWithBaseClassEnable(true).setGenerationStrategy(entityGenerationStrategy)
        ;

        if (isTree) {
            globalConfig.getEntityConfig().setSuperClass(TreeEntity.class);
        } else {
            globalConfig.getEntityConfig().setSuperClass(entityClass);
//            globalConfig.getEntityConfig().setSuperClass(SuperEntity.class);
        }

        globalConfig.enableVo()
                .setSourceDir(globalConfig.getSourceDir() + "/workbench-pojo")
                .setWithLombok(true)
                .setWithSwagger(true)
                .setWithExcel(true)
                .setGenerationStrategy(entityGenerationStrategy)
//                .setImplInterfaces(Serializable.class)
        ;

        if (isTree) {
            globalConfig.getVoConfig()
                    .setSuperClass(TreeEntity.class)
                    .setGenericityType(idClass).setGenerationStrategy(entityGenerationStrategy);
        }
        globalConfig.enableQuery()
                .setSourceDir(globalConfig.getSourceDir() + "/workbench-pojo")
                .setSuperClass(ExtraParams.class)
                .setGenerationStrategy(entityGenerationStrategy)
                .setWithLombok(true).setWithSwagger(true).setWithExcel(true)
                .setImplInterfaces(Serializable.class);

//
        globalConfig.enableDto()
                .setSourceDir(globalConfig.getSourceDir() + "/workbench-pojo")
                .setWithLombok(true).setWithSwagger(true).setWithValidator(true)
                .setGenerationStrategy(entityGenerationStrategy)
                //.setImplInterfaces(Serializable.class)
                .setIgnoreColumns(new HashSet<>(Arrays.asList(SuperEntity.CREATED_AT_FIELD, SuperEntity.CREATED_BY_FIELD,
                        SuperEntity.UPDATED_AT_FIELD, SuperEntity.UPDATED_BY_FIELD,
                        SuperEntity.DELETED_AT_FIELD, SuperEntity.DELETED_BY_FIELD)));

        globalConfig.enableController()
                .setSourceDir(globalConfig.getSourceDir() + "/workbench-web")
                .setRequestMappingPrefix("/" + globalConfig.getPackageConfig().getModule())
                .setWithCrud(true)
//                .setSuperClass(SuperController.class)
//                .setSuperClass(SuperWriteController.class)
//                .setSuperClass(SuperReadController.class)
//               .setSuperClass(SuperSimpleController.class)
                .setGenerationStrategy(GenerationStrategyEnum.OVERWRITE);
//                .setGenerationStrategy(GenerationStrategyEnum.EXIST_IGNORE);

        globalConfig.getControllerConfig().setSuperClass(SuperController.class);

        globalConfig.enableMapperXml();

//        //设置生成 mapper
        globalConfig.enableMapper().setSourceDir(globalConfig.getSourceDir() + "/workbench-dao").setSuperClass(SuperMapper.class).setGenerationStrategy(GenerationStrategyEnum.EXIST_IGNORE);
        globalConfig.enableService().setSourceDir(globalConfig.getSourceDir() + "/workbench-service").setSuperClass(SuperService.class).setGenerationStrategy(GenerationStrategyEnum.EXIST_IGNORE);
        globalConfig.enableServiceImpl().setSourceDir(globalConfig.getSourceDir() + "/workbench-service").setSuperClass(SuperServiceImpl.class).setGenerationStrategy(GenerationStrategyEnum.EXIST_IGNORE);
//        globalConfig.enableFront();
        globalConfig.disableFrontTable();

        //可以单独配置某个列
//        ColumnConfig columnConfig = new ColumnConfig();
//        columnConfig.setColumnName("tenant_id");
//        columnConfig.setLarge(true);
//        columnConfig.setVersion(true);
//        globalConfig.setColumnConfig("sys_param", columnConfig);

        JdbcTypeMapping.setTypeMapper(new JdbcTypeMapping.JdbcTypeMapper() {
            @Override
            public String getType(String rawType, String jdbcType, Table table, Column column) {

                return null;
            }
        });


        return globalConfig;
    }

    public static GlobalConfig createOpenBackend() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        // TODO 是否树形结构
        GenerationStrategyEnum entityGenerationStrategy = GenerationStrategyEnum.OVERWRITE;
        boolean isTree = false;
        Class<?> idClass = Long.class;
        Class<?> entityClass = top.mddata.base.base.entity.BaseEntity.class;  // 3个字段
//        Class<?> entityClass = top.mddata.base.base.entity.SuperEntity.class;   // 5个字段
        // 设置根包和生成的表名
        //globalConfig.setBasePackage("com.fsfsfs.demo.test");
        globalConfig.setSourceDir(System.getProperty("user.dir") + "/mdp-apps/md-open");
        globalConfig.setBasePackage("top.mddata.open");
//        globalConfig.getPackageConfig().setSubSystem("").setModule("permission");
//        globalConfig.getPackageConfig().setSubSystem("").setModule("organization");
        globalConfig.getPackageConfig().setSubSystem("").setModule("admin");

//        globalConfig.setGenerateTable("mdo_app_group_rel",
//                "mdo_group_api_rel",
//                "mdo_group_scope_rel",
//                "mdo_oauth_log",
//                "mdo_oauth_openid",
//                "mdo_oauth_unionid"
//        );

//        globalConfig.setGenerateTable("mdo_event_push", "mdo_notify_info", "mdo_event_type");
        globalConfig.setGenerateTable("mdo_event_push_log", "mdo_notify_info_log", "mdo_event_trigger");
//        globalConfig.setGenerateTable("mdo_api_call_log", "mdo_event_subscription", "mdo_notify_log");
//        globalConfig.setGenerateTable("mdo_help_doc", "mdo_notify_info", "mdo_event_type");
//        globalConfig.setGenerateTable("mdo_app_keys");
//        globalConfig.setGenerateTable("mdo_api",
//                "mdo_app",
//                "mdo_app_apply",
//                "mdo_app_keys",
//                "mdo_doc_content",
//                "mdo_doc_group",
//                "mdo_doc_info",
//                "mdo_oauth_scope",
//                "mdo_scope_group");

//        globalConfig.setEntityGenerateEnable();

        //设置表前缀和只生成哪些表
        globalConfig.setTablePrefix("mdo_");
        globalConfig.setJdkVersion(17);
        globalConfig.getJavadocConfig()
                .setAuthor("henhen6")
                .setColumnCommentFormat(comment -> StrUtil.replace(comment, "\n", "\n     * "))
                .setColumnSwaggerCommentFormat(comment -> StrUtil.replace(StrUtil.subBefore(StrUtil.subBefore(comment, "\r\n", false), "\n", false), "\"", "\\\""))
                .setTableCommentFormat(comment -> StrUtil.replace(comment, "\n", "\n     * "))
                .setTableSwaggerCommentFormat(comment -> StrUtil.replace(StrUtil.subBefore(StrUtil.subBefore(comment, "\r\n", false), "\n", false), "\"", "\\\""))
        ;

//        globalConfig.setTableConfig(TableConfig.create()
//                .setInsertListenerClass(DefaultInsertListener.class)
//                .setUpdateListenerClass(DefaultUpdateListener.class));


        globalConfig.enableEntity()
                .setSourceDir(globalConfig.getSourceDir() + "/open-pojo")
                .setGenericityType(idClass)
                .setWithLombok(true)
                .setWithBaseClassEnable(true).setGenerationStrategy(entityGenerationStrategy)
        ;

        if (isTree) {
            globalConfig.getEntityConfig().setSuperClass(TreeEntity.class);
        } else {
            globalConfig.getEntityConfig().setSuperClass(entityClass);
//            globalConfig.getEntityConfig().setSuperClass(SuperEntity.class);
        }

        globalConfig.enableVo()
                .setSourceDir(globalConfig.getSourceDir() + "/open-pojo")
                .setWithLombok(true)
                .setWithSwagger(true)
                .setWithExcel(true)
                .setGenerationStrategy(entityGenerationStrategy)
//                .setImplInterfaces(Serializable.class)
        ;

        if (isTree) {
            globalConfig.getVoConfig()
                    .setSuperClass(TreeEntity.class)
                    .setGenericityType(idClass).setGenerationStrategy(entityGenerationStrategy);
        }
        globalConfig.enableQuery()
                .setSourceDir(globalConfig.getSourceDir() + "/open-pojo")
                .setSuperClass(ExtraParams.class)
                .setGenerationStrategy(entityGenerationStrategy)
                .setWithLombok(true).setWithSwagger(true).setWithExcel(true)
                .setImplInterfaces(Serializable.class);

//
        globalConfig.enableDto()
                .setSourceDir(globalConfig.getSourceDir() + "/open-pojo")
                .setWithLombok(true).setWithSwagger(true).setWithValidator(true)
                .setGenerationStrategy(entityGenerationStrategy)
                //.setImplInterfaces(Serializable.class)
                .setIgnoreColumns(new HashSet<>(Arrays.asList(SuperEntity.CREATED_AT_FIELD, SuperEntity.CREATED_BY_FIELD,
                        SuperEntity.UPDATED_AT_FIELD, SuperEntity.UPDATED_BY_FIELD,
                        SuperEntity.DELETED_AT_FIELD, SuperEntity.DELETED_BY_FIELD)));

        globalConfig.enableController()
                .setSourceDir(globalConfig.getSourceDir() + "/open-web")
                .setRequestMappingPrefix("/" + globalConfig.getPackageConfig().getModule())
                .setWithCrud(true)
//                .setSuperClass(SuperController.class)
//                .setSuperClass(SuperWriteController.class)
//                .setSuperClass(SuperReadController.class)
//               .setSuperClass(SuperSimpleController.class)
//                .setGenerationStrategy(GenerationStrategyEnum.OVERWRITE);
                .setGenerationStrategy(GenerationStrategyEnum.EXIST_IGNORE);

        globalConfig.getControllerConfig().setSuperClass(SuperController.class);

        globalConfig.enableMapperXml();

//        //设置生成 mapper
        globalConfig.enableMapper().setSourceDir(globalConfig.getSourceDir() + "/open-dao").setSuperClass(SuperMapper.class).setGenerationStrategy(GenerationStrategyEnum.EXIST_IGNORE);
        globalConfig.enableService().setSourceDir(globalConfig.getSourceDir() + "/open-service").setSuperClass(SuperService.class).setGenerationStrategy(GenerationStrategyEnum.EXIST_IGNORE);
        globalConfig.enableServiceImpl().setSourceDir(globalConfig.getSourceDir() + "/open-service").setSuperClass(SuperServiceImpl.class).setGenerationStrategy(GenerationStrategyEnum.EXIST_IGNORE);
        globalConfig.disableFrontTable();

        return globalConfig;
    }

}
