import java.awt.Color
import java.awt.Font
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import javax.swing.*
import kotlin.math.sqrt

class Calculator : JFrame("wu"), ActionListener {
    private val KEYS = arrayOf(
        "7",
        "8",
        "9",
        "AC",
        "4",
        "5",
        "6",
        "-",
        "1",
        "2",
        "3",
        "+",
        "0",
        "pi",
        "del",
        "/",
        "sqrt",
        "%",
        "x*x",
        "*",
        "(",
        ")",
        ".",
        "="
    )
    private val keys = arrayOfNulls<JButton>(KEYS.size)
    private val resultText = JTextArea("0.0")
    private val History = JTextArea()
    private val jp2 = JPanel()
    private val gdt1 = JScrollPane(resultText)
    private val gdt2 = JScrollPane(History)
    private val label = JLabel("历史记录")
    private var input = ""

    init {
        resultText.setBounds(20, 18, 255, 115)
        resultText.setAlignmentX(RIGHT_ALIGNMENT)
        resultText.isEditable = false
        resultText.setFont(Font("monospaced", Font.PLAIN, 18))
        History.setFont(Font("monospaced", Font.PLAIN, 18))
        History.setBounds(290, 40, 250, 370)
        History.setAlignmentX(LEFT_ALIGNMENT)
        History.isEditable = false
        label.setBounds(300, 15, 100, 20)
        jp2.setBounds(290, 40, 250, 370)
        jp2.setLayout(GridLayout())
        val jp1 = JPanel()
        jp1.setBounds(20, 18, 255, 115)
        jp1.setLayout(GridLayout())
        resultText.setLineWrap(true)
        resultText.setWrapStyleWord(true)
        resultText.setSelectedTextColor(Color.RED)
        History.setLineWrap(true)
        History.setWrapStyleWord(true)
        History.setSelectedTextColor(Color.blue)
        gdt1.setViewportView(resultText)
        gdt2.setViewportView(History)
        gdt1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS)
        gdt2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS)
        gdt2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS)
        jp1.add(gdt1)
        jp2.add(gdt2)
        this.add(jp1)
        this.add(jp2)
        this.layout = null
        this.add(label)
        var x = 20
        var y = 150
        for (i in KEYS.indices) {
            keys[i] = JButton()
            keys[i]!!.setText(KEYS[i])
            keys[i]!!.setBounds(x, y, 60, 40)
            if (x < 215) {
                x += 65
            } else {
                x = 20
                y += 45
            }
            this.add(keys[i])
        }
        for (i in KEYS.indices) {
            keys[i]!!.addActionListener(this)
        }
        setResizable(false)
        this.setBounds(500, 200, 567, 480)
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        this.isVisible = true
    }

    // 事件处理
    override fun actionPerformed(e: ActionEvent) {
        var label = e.actionCommand
        if (label == "AC") //清空按钮，消除显示屏文本框前面所有的输入和结果
        {
            input = ""
            resultText.text = "0.0"
        } else if (label == "sqrt") {
            val n: String
            n = if (input.isEmpty()) "error!" //加判断，先输入数字再输入开方符号才是合法的
            else kfys(input).toString()
            resultText.text = "sqrt($input)=$n"
            History.text = History.getText() + resultText.getText() + "\n"
            input = n
        } else if (label == "x*x") {
            val m: String
            m = if (input.isEmpty()) "error!" else pfys(input).toString()
            resultText.text = "$input^2=$m"
            History.text = History.getText() + resultText.getText() + "\n"
            input = m
        } else if (label == "=") {
            if (input.isEmpty()) return
            val s = houzhui(input)
            val result = Result(s)
            resultText.text = "$input=$result"
            History.text = History.getText() + resultText.getText() + "\n"
        } else if (label == "del") {
            var str = resultText.getText()
            if (str.length > 0) {
                str = str.substring(0, str.length - 1)
            }
            input = str
            resultText.text = str
        } else {
            if (label == "pi") {
                val m = 3.14159.toString()
                label = m
            }
            input = input + label
            resultText.text = input
        }
    }

    //将中缀表达式转换为后缀表达式
    private fun houzhui(infix: String): Array<String?> {  //infix 中缀
        var s = "" // 用于承接多位数的字符串
        val opStack = Stack<String>()
        val postQueue = Stack<String>()
        println("中缀：$infix")
        run {
            var i = 0
            while (i < infix.length) {
                if ("1234567890.".indexOf(infix[i]) >= 0) {    // 遇到数字字符直接入队
                    s = ""
                    while (i < infix.length && "0123456789.".indexOf(infix[i]) >= 0) {
                        s = s + infix[i]
                        i++
                    }
                    i--
                    postQueue.push(s)
                } else if ("(".indexOf(infix[i]) >= 0) {
                    opStack.push(infix[i].toString())
                } else if (")".indexOf(infix[i]) >= 0) {
                    while (opStack.peek() != "(") {
                        postQueue.push(opStack.pop())
                    }
                    opStack.pop() //删除左括号
                } else if ("*%/+-".indexOf(infix[i]) >= 0) {
                    if (opStack.empty() || "(".contains(opStack.peek())) {
                        opStack.push(infix[i].toString())
                    } else {
                        val rule =
                            "*%/+-".contains(opStack.peek()) && "+-".indexOf(infix[i]) >= 0 || "*%/".contains(
                                opStack.peek()
                            ) && "*%/".indexOf(infix[i]) >= 0
                        while (!opStack.empty() && rule) {
                            postQueue.push(opStack.peek())
                            opStack.pop()
                        }
                        opStack.push(infix[i].toString())
                    }
                }
                i++
            }
        }
        while (!opStack.empty()) {
            postQueue.push(opStack.pop())
        }
        val suffix = arrayOfNulls<String>(postQueue.size)
        for (i in postQueue.indices.reversed()) {
            suffix[i] = postQueue.pop()
        }
        println("后缀：" + suffix.clone().contentToString())
        return suffix
    }

    //开方运算方法
    fun kfys(str: String): Double {
        val a = str.toDouble()
        return sqrt(a)
    }

    //平方运算方法
    fun pfys(str: String): Double {
        val a = str.toDouble()
        return a*a
    }

    // 计算后缀表达式，并返回最终结果
    fun Result(suffix: Array<String?>): String? {
        val Result = Stack<String?>()
        var i: Int
        i = 0
        while (i < suffix.size) {
            if ("1234567890.".indexOf(suffix[i]!![0]) >= 0) {
                Result.push(suffix[i])
            } else {
                var x: Double
                var y: Double
                var n = 0.0
                x = Result.pop()!!.toDouble()
                y = Result.pop()!!.toDouble()
                when (suffix[i]) {
                    "*" -> n = y * x
                    "/" -> n = if (x == 0.0) {
                        return "error!"
                    } else y / x

                    "%" -> n = if (x == 0.0) {
                        return "error!"
                    } else y % x

                    "-" -> n = y - x
                    "+" -> n = y + x
                }
                Result.push(n.toString())
            }
            i++
        }
        println("return:" + Result.peek())
        return Result.peek() // 返回最终结果
    }

    companion object {
        // 主函数
        @JvmStatic
        fun main(args: Array<String>) {
            val a = Calculator()
        }
    }
}
