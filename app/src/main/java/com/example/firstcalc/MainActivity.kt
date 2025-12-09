package com.example.firstcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
/*import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert*/
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.firstcalc.ui.theme.FirstCalcTheme
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.ArrayDeque

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                // Scaffold with top app bar and overflow menu
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Calculator") },
                            actions = {
                                var expanded by remember { mutableStateOf(false) }
                                IconButton(onClick = { expanded = true }) {
                                    Text("⋮", fontSize = 20.sp)
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(text = { Text("Settings") }, onClick = {
                                        expanded = false
                                        navController.navigate("settings")
                                    })
                                    DropdownMenuItem(text = { Text("About") }, onClick = {
                                        expanded = false
                                        navController.navigate("about")
                                    })
                                }
                            }
                        )

                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "calculator",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("calculator") { CalculatorScreen() }
                        composable("settings") { SettingsScreen(navController) }
                        composable("about") { AboutScreen(navController) }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {

    var display by remember { mutableStateOf("0") }


    // Rows (4 columns each). Last row uses "00", "0", ".", "="
    val rows = listOf(
        listOf("AC", "\u232B", "%", "\u00F7"), // \u232B = ⌫, \u00F7 = ÷
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("00", "0", ".", "=")
    )
    var preview by remember { mutableStateOf("") }


    // simple click handler for layout demo (replace with your compute logic)
    fun onButtonClick(label: String) {
        when (label) {
            "AC" -> display = "0"
            "\u232B" -> { // backspace
                display = if (display.length <= 1) "0" else display.dropLast(1)
            }
            "=" -> {
                // TODO: replace with proper evaluation (BigDecimal or expression parser)
                // naive eval placeholder: try simple Double eval for "a op b" only
                display = try {
                    // very naive: handle simple expressions like "12+3" (not recommended for production)
                    val result = SimpleCalculator.eval(display)
                    result
                } catch (_: Exception) {
                    "Error"
                }
            }
            else -> {
                // append digit/operator; prevent leading zeros like "00" turning "0" -> "00" undesirably
                display = if (display == "0" && label != ".") label else display + label
            }
        }
        preview = display    // preview always shows current expression

    }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            // Display area: read-only OutlinedTextField so keyboard won't pop
            OutlinedTextField(
                value = display,
                onValueChange = { /* read-only */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 30.sp, textAlign = TextAlign.End),
                readOnly = true,
                trailingIcon = null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
            Text(
                text = preview,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.End
            )


            // Space between display and grid
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

            // Button grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { label ->
                            CalculatorCircleButton(
                                label = label,
                                onClick = { onButtonClick(label) },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorCircleButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bg: Color = Color(0xFF4F6280),
    contentColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = contentColor, fontSize = 18.sp, textAlign = TextAlign.Center)
    }
}

/**
 * Very tiny, unsafe evaluator for demo only.
 * Replace this with a proper parser (shunting-yard) or BigDecimal-based evaluation.
 */
object SimpleCalculator {
    private val opPrecedence = mapOf(
        "+" to 1,
        "-" to 1,
        "*" to 2,
        "/" to 2,
        "×" to 2,
        "\u00F7" to 2, // ÷
        "u" to 3 // unary minus (internal token)
    )

    private val rightAssociative = setOf("u")

    /**
     * Evaluate an expression string and return a formatted result.
     * Supports: + - * / ( ) unary minus, ×, ÷, and postfix percent (%) on numbers.
     * Uses BigDecimal for arithmetic. Division uses scale 10 with HALF_UP rounding.
     */
    fun eval(expr: String): String {
        val tokens = tokenize(expr)
        val rpn = shuntingYard(tokens)
        val result = evalRPN(rpn)
        // strip trailing zeros and use plain string (no scientific notation)
        val plain = result.stripTrailingZeros().toPlainString()
        return plain
    }

    private fun tokenize(input: String): List<String> {
        val s = input.replace("×", "*").replace("\u00F7", "/").replace('÷', '/')
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < s.length) {
            val c = s[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < s.length && (s[i].isDigit() || s[i] == '.')) {
                        sb.append(s[i])
                        i++
                    }
                    tokens.add(sb.toString())
                }
                c == '+' || c == '*' || c == '/' -> {
                    tokens.add(c.toString())
                    i++
                }
                c == '-' -> {
                    // decide if unary minus: if at start or after '(' or another operator
                    val prev = tokens.lastOrNull()
                    if (prev == null || prev == "(" || prev in opPrecedence.keys || prev == "u") {
                        tokens.add("u") // unary minus internal token
                    } else {
                        tokens.add("-")
                    }
                    i++
                }
                c == '(' || c == ')' -> {
                    tokens.add(c.toString()); i++
                }
                c == '%' -> {
                    tokens.add("%"); i++
                }
                else -> throw IllegalArgumentException("Invalid char '${c}' in expression")
            }
        }
        return tokens
    }

    private fun shuntingYard(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val ops = ArrayDeque<String>()
        for (t in tokens) {
            when {
                t.matches(Regex("""\d+(\.\d+)?""")) -> output.add(t)
                t == "%" -> {
                    // percent is postfix: push directly to output (applies to previous number/result)
                    output.add(t)
                }
                t == "(" -> ops.push(t)
                t == ")" -> {
                    while (ops.isNotEmpty() && ops.peek() != "(") output.add(ops.pop())
                    if (ops.isEmpty() || ops.peek() != "(") throw IllegalArgumentException("Mismatched parentheses")
                    ops.pop()
                }
                opPrecedence.containsKey(t) || t == "-" || t == "+" || t == "u" -> {
                    val o1 = t
                    while (ops.isNotEmpty()) {
                        val o2 = ops.peek()
                        if (o2 == "(") break
                        val p1 = opPrecedence[o1] ?: 0
                        val p2 = opPrecedence[o2] ?: 0
                        if ( (rightAssociative.contains(o1) && p1 < p2) ||
                            (!rightAssociative.contains(o1) && p1 <= p2)
                        ) {
                            output.add(ops.pop())
                        } else break
                    }
                    ops.push(o1)
                }
                else -> throw IllegalArgumentException("Unknown token: $t")
            }
        }
        while (ops.isNotEmpty()) {
            val top = ops.pop()
            if (top == "(" || top == ")") throw IllegalArgumentException("Mismatched parentheses")
            output.add(top)
        }
        return output
    }

    private fun evalRPN(rpn: List<String>): BigDecimal {
        val stack = ArrayDeque<BigDecimal>()
        for (t in rpn) {
            when {
                t.matches(Regex("""\d+(\.\d+)?""")) -> stack.push(BigDecimal(t))
                t == "%" -> {
                    val v = stack.pollFirst() ?: throw IllegalArgumentException("Bad expression (percent)")
                    stack.push(v.divide(BigDecimal(100), 10, RoundingMode.HALF_UP).stripTrailingZeros())
                }
                t == "u" -> {
                    val v = stack.pollFirst() ?: throw IllegalArgumentException("Bad expression (unary)")
                    stack.push(v.negate())
                }
                t in setOf("+", "-", "*", "/", "×", "\u00F7") -> {
                    val b = stack.pollFirst() ?: throw IllegalArgumentException("Bad expression (operand)")
                    val a = stack.pollFirst() ?: throw IllegalArgumentException("Bad expression (operand)")
                    val res = when (t) {
                        "+" -> a.add(b)
                        "-" -> a.subtract(b)
                        "*", "×" -> a.multiply(b)
                        "/", "\u00F7" -> {
                            if (b.compareTo(BigDecimal.ZERO) == 0) throw ArithmeticException("Division by zero")
                            // use a scale to avoid non-terminating decimal exception
                            a.divide(b, 10, RoundingMode.HALF_UP)
                        }
                        else -> throw IllegalArgumentException("Unknown operator $t")
                    }
                    stack.push(res)
                }
                else -> throw IllegalArgumentException("Unknown token in RPN: $t")
            }
        }
        if (stack.size != 1) throw IllegalArgumentException("Bad expression")
        return stack.single().stripTrailingZeros()
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    FirstCalcTheme {
        CalculatorScreen()
    }
}


@Composable
fun SettingsScreen(navController: androidx.navigation.NavHostController) {
    // Simple example settings screen with a back button
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}

@Composable
fun AboutScreen(navController: androidx.navigation.NavHostController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("About", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
        Text(text = "Hello I'm Joyprokash.\nIts my first kotlin project.\nI hope you like it.\nThanks for using my app.")
    }
}
