# **代码规范与审核流程**

> 编码规范：[阿里巴巴Java开发手册终极版v1.3.0.pdf](https://www.w3cschool.cn/alibaba_java/)

#### **为什么要制定规范**

古话说，没有规矩不成方圆。在团队协作开发时，每个人提交代码时都会写 commit message，但如果没有规范，每个人都会有自己的书写风格，因此在代码审核时经常看到的是五花八门，十分不利于阅读和维护。

#### **开发规范**

**Git Commit message提交信息规范**

模板:

```
[Type]问题摘要(issues链接)
```

示例:

```
[feat]增加企业微信自动化配置功能(https://github.com/easywecom/easyWeCom_Dashboard/issues/2)
[fix]修复项目重启导致需要重新登录问题(https://github.com/easywecom/easyWeCom_Dashboard/issues/2)[docs]修改初始化数据库脚本(https://github.com/easywecom/easyWeCom_Dashboard/issues/2)
```

Type 类型必须是下面之一，并且为小写:

```yaml
feat: 修改/增加新功能
fix: 修改bug的变更
docs: 文档相关变更
style: 不影响代码含义的变更(空白、格式、缺少符号等)
refactor: 代码重构变更
perf: 改进性能的变更
test: 添加/修改现有的单元测试
chore: Build, .gitignore或辅助工具、库(如文档生成)等变更
```

#### **分支创建规范**

模板:

```
feat/author-issues序号
```

示例:

```
例如：开发者developer1开发增加企业微信自动化配置功能, issues序号为41852
那么分支创建为: feat/developer1-41852
   
例如：开发developer2修复项目重启导致需要重新登录问题, issues序号为41942
那么分支创建为: fix/developer2-41942
```

如何查看issues序号

![](https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/06/01/screenshot-20220601-143308.png)

#### **update.sql 文件更新规范**

每个sql更新都要加上注释和时间点和issues序号

❌ 错误

```sql
-- 添加注释
ALTER TABLE `we_flower_customer_rel` MODIFY COLUMN `status` char(2) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除流失 2员工删除用户）';
```

✅ 正确

```sql
-- author 2021.xx.xx 客户员工关系表字段status增加注释((https://github.com/easywecom/easyWeCom_Dashboard/issues/2)
ALTER TABLE `we_flower_customer_rel` MODIFY COLUMN `status` char(2) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除流失 2员工删除用户）';
```

#### **开发评审规范**

##### **代码提交合并流程** ：

如果你打算开始处理一个 issue，请先检查一下 issue 下面的留言，确认没有其他开发者正在处理这个 issue后，你可以留言告知其他人你将会处理这个 issue，以免其他开发者重复处理。

如果之前有人留言说会处理这个 issue 但是一两个星期都没有动静，那么你也可以接手处理这个 issue，当然还是需要留言告知其他人。

所有的代码提交前，开发者必须在本地使用SonarLint对当前模块代码进行检查，并修改完所有的异常提示后，方可提交代码。

我们会关注所有的 pull request，对其进行 review 以及合并你的代码，也有可能要求你做一些修改或者告诉你我们为什么不能接受这样的修改。