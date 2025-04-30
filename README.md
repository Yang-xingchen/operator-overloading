# operator-overloading
实现Java版本的操作符重载功能。以运算符方式使用`BigDecimal`及`BigInteger`类库。

> [!IMPORTANT]
> 目前还在早期demo阶段，请勿使用于生产环境，欢迎提交issues及PR

使用示例: [Main.java](example/src/main/java/org/yangxc/example/Main.java)

# TODO LIST
- [ ] 支持科学计数法, 如: `-1.23e-4`
- [ ] 支持变量类型转化, 如: `(int)a`(a为任意已注册转换方法类型的变量, 如a为`BigDecimal`类型，将转化为`a.intValueExact()`)
- [ ] 支持`(exp)`语法, 如: `(1+2)*3`
- [ ] 自动化导入(消除`spi`使用)
- [ ] 支持自定义类的操作运算符重载
- [ ] 支持变量调用方法
- [ ] 支持调用静态方法
- [ ] 支持条件分支
- [ ] 支持`Spring`自动注入(添加其示例)
- [ ] 文档
- [ ] 支持自定义操作符
- [ ] _内置其他数学类，如: 矩阵_
- [ ] _转移至`java8`版本_
- [ ] ...