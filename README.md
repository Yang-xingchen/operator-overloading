# operator-overloading
实现Java版本的操作符重载功能。以运算符方式使用`BigDecimal`及`BigInteger`类库。

> [!IMPORTANT]
> 目前还在早期demo阶段，请勿使用于生产环境，欢迎提交issues及PR

使用示例: [Main.java](example/src/main/java/org/yangxc/example/Main.java)

# HOW TO USE
默认使用`NumberType#BIG_DECIMAL`模式，该模式下表达式内的数字转化为`java.math.BigDecimal`。
支持`NumberType#BIG_INTEGER`(该模式转成`java.math.BigInteger`)及`NumberType#PRIMITIVE`(该模式使用原始类型`int`, `long`, `double`)。

1. 配置spi(后续考虑移除该步骤)，参考见[此处](example/src/main/resources/META-INF/services/javax.annotation.processing.Processor): 
   1. 添加`META-INF/services/javax.annotation.processing.Processor`文件，若已存在则忽略该步骤
   2. 在该文件内添加`org.yangxc.operatoroverloading.core.processor.ServiceProcessor`(作为单独一行)
2. 定义服务接口:
    ```
   @OperatorService
   public intefact BaseService {}
   ```
   1. 该类型必须为接口
   2. 必须添加`annotation.org.yangxc.operatoroverloading.core.OperatorService`注解，具体参数见注释[OperatorService.java](core/src/main/java/org/yangxc/core/annotation/OperatorService.java)
   3. 定义方法, 添加`annotation.org.yangxc.operatoroverloading.core.OperatorFunction`注解，具体参数见注释[OperatorFunction.java](core/src/main/java/org/yangxc/core/annotation/OperatorFunction.java)
   4. _无需实现类_
3. 获取实现: `org.yangxc.operatoroverloading.core.Overloading.get(BaseService.class)`

## Example & Explain
> 该示例仅作为展示功能，如果你仔细分析，会发现无论参数如何该示例返回结果总是相同的，但作为示例已经足够

定义:
```java
@OperatorService(imports = BigDecimal.class)                                                                        // 1
public interface BaseService {                                                                                      // 2

    @OperatorFunction(                                                                                              // 3
            statements = {                                                                                          // 4
                    @Statement(type = BigDecimal.class, varName = "a", exp = "-12.34e2-.56-(.78e-2+(BigDecimal)b)") // 5
            },
            value = "a+(BigDecimal)b"                                                                               // 6
    )
    double test(int b);                                                                                             // 7

}
```

使用:
```java
BaseService service = Overloading.get(BaseService.class);                                                           // 8
// 结果为: -1234.5678
double res = service.test(1);                                                                                       // 9
```

- 在`2`处定义了一个接口
  - 在`1`处声明该接口需要被处理
  - 在`1`处声明导入`BigDecimal`类，之后可在表达式中使用`(BigDecimal)`进行强转，如`5`和`6`中`(BigDecimal)b"`是将入参`b`(原为`int`型转成`BigDecimal`进行计算。
  - 在`1`处未定义`numberType`，默认使用`java.math.BigDecimal`处理表达式内的数字
  - 在`1`处未定义`value`，默认使用接口名称+`Impl`作为实现类的类名，即`BaseServiceImpl`
- 在`7`处定义一个方法，入参为`int`类型的`b`，在`3`处定义该方法内容
  - 在`5`处定义一个变量`a`
    - 类型为`BigDecimal`
    - 其表达式含义为`-12.34e2`(实际值为`-1234`)减`.56`(实际值为`0.56`)减(`.78e-2`(实际值为`0.0078`)加上`(BigDecimal)b`(将`b`转为`BigDecimal`运算)), 该表达式化简后为`1234.5678-b`
    - 在`5`处未定义`numberType`，默认使用方法的`numberType`处理表达式内的数字，即`NumberType#BIG_DECIMAL`
  - 在`6`处定义该方法的结果表达式，为本地变量`a`加上入参`b`转为`BigDecimal`的结果
  - 由于`7`处定义方法的返回类型为`double`, 定义的操作类型为`BigDecimal`，故进行转化
  - 在`3`处未定义`numberType`，默认使用接口的`numberType`处理表达式内的数字，即`NumberType#BIG_DECIMAL`
- 在`8`处获取该类实现对象，在`9`处调用定义的方法

> [!TIP]
> `statements`表示定义本地变量，可定义多个变量。入参、变量、返回的依赖关系需自行处理，程序会按照定义顺序添加代码。

> [!TIP]
> 表达式可定义在`@OperatorFunction`的`value`(表示该方法的返回值)及`@Statement`的`exp`(表示该变量的运算表达式)
> - 表达式忽略空格、制表符、换行符
> - 可使用方法入参或者定义的变量，变量类型不支持泛型，需符合操作(即方法入参要求的类型和变量类型需一致，若不一致需要转化，程序**不会**自动进行转化)
> - 可使用数字，会自动转换为`numberType`定义的类型，`BigDecimal`及`BigInteger`将使用表达式内的字符串进行转化
> - 数字定义同Java语法(十进制)，支持`1`(整数), `1_000`(下划线分割), `1.23`(小数), `.23`(忽略整数的小数), `-.23`(负数，需在表达式开头), `+.23`(正数，需在表达式开头), `1e2`(科学计数法，`e`大小写皆可), `1L`(`L`结尾，`L`大小写皆可，注意程序将忽略`L`, 如需定义`long`，需手动进行转化)
> - 支持`+`, `-`, `*`, `/`, `%`运算及`()`使用子表达式
> - 支持使用`(type)exp`将`exp`转成`type`类型。如果`exp`为整个表达式结果，可自动获取返回类型或变量类型进行转化
> - 内置基本类型(除`boolean`), `String`, `BigDecimal`, `BigInteger`之间互相转化，见[ClassOverloading](core/src/main/java/org/yangxc/core/constant/ClassOverloading.java)
> - `BigDecimal`及`BigInteger`转化为基本类型使用`xxxValueExact()`

该示例编译后生成的代码如下:
```java
public class BaseServiceImpl implements BaseService {

    @Override
    public double test(int b) {
        BigDecimal a = new BigDecimal("-12.34e2").subtract(new BigDecimal("0.56")).subtract(new BigDecimal("0.78e-2").add(new BigDecimal(b)));
        return a.add(new BigDecimal(b)).doubleValue();
    }
    
}
```

# TODO LIST
> _斜体_为未计划，不一定会做

- [ ] 支持将科学计数法转成普通数值
- [ ] 自动化导入(消除`spi`使用)
- [ ] 支持自定义类的操作运算符重载
- [ ] 支持变量调用方法
- [ ] 支持调用静态方法
- [ ] 支持条件分支
- [ ] 支持`Spring`自动注入(添加其示例)
- [ ] 文档
- [ ] 支持自定义操作符
- [ ] _支持十六进制、八进制、二进制及转成十进制_
- [ ] _支持泛型_
- [ ] _内置其他数学类，如: 矩阵_
- [ ] _转移至`java8`版本_
- [ ] ...