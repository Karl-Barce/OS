package com.example.testforos

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView // Add this import statement
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextProcesses: EditText
    private lateinit var buttonCalculate: Button
    private lateinit var tableLayoutResult: TableLayout

    private lateinit var burstTimes: IntArray
    private lateinit var arrivalTimes: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextProcesses = findViewById(R.id.editTextProcesses)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        tableLayoutResult = findViewById(R.id.tableLayoutResult)

        buttonCalculate.setOnClickListener {
            calculateSJF()
        }
    }

    private fun calculateSJF() {
        // Clear previous results
        tableLayoutResult.removeAllViews()

        val n = editTextProcesses.text.toString().toInt()

        if (n <= 0) {
            return
        }

        burstTimes = IntArray(n)
        arrivalTimes = IntArray(n)

        // Dynamically create EditTexts for burst times and arrival times
        for (i in 0 until n) {
            val editTextBurstTime = EditText(this)
            editTextBurstTime.hint = "Burst Time for Process ${i + 1}"
            editTextBurstTime.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            tableLayoutResult.addView(editTextBurstTime)

            val editTextArrivalTime = EditText(this)
            editTextArrivalTime.hint = "Arrival Time for Process ${i + 1}"
            editTextArrivalTime.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            tableLayoutResult.addView(editTextArrivalTime)

            burstTimes[i] = 0
            arrivalTimes[i] = 0
        }

        val calculateButton = Button(this)
        calculateButton.text = "Calculate"
        calculateButton.setOnClickListener {
            // Update burstTimes and arrivalTimes arrays with values from EditText elements
            for (i in 0 until n) {
                burstTimes[i] = (tableLayoutResult.getChildAt(i * 2) as EditText).text.toString().toInt()
                arrivalTimes[i] = (tableLayoutResult.getChildAt(i * 2 + 1) as EditText).text.toString().toInt()
            }

            val processOrder = calculateSJFOrder()
            val waitingTimes = calculateWaitingTime(processOrder)
            val turnaroundTimes = calculateTurnaroundTime(waitingTimes)

            displayResults(processOrder, waitingTimes, turnaroundTimes)
        }

        tableLayoutResult.addView(calculateButton)
    }






    private fun calculateSJFOrder(): IntArray {
        val n = burstTimes.size
        val processOrder = IntArray(n) { it }

        // Create a custom comparator for sorting based on burst time and arrival time
        val comparator = compareBy<Int> { index -> arrivalTimes[index] }.thenBy { index -> burstTimes[index] }

        // Sort processes based on burst time and arrival time using SJF algorithm
        processOrder.sortedWith(comparator)

        return processOrder
    }





    private fun calculateWaitingTime(processOrder: IntArray): IntArray {
        val n = burstTimes.size
        val waitingTimes = IntArray(n)

        waitingTimes[0] = 0 // Waiting time for the first process is always 0

        for (i in 1 until n) {
            waitingTimes[i] = waitingTimes[i - 1] + burstTimes[processOrder[i - 1]]
        }

        return waitingTimes
    }

    private fun calculateTurnaroundTime(waitingTimes: IntArray): IntArray {
        val n = burstTimes.size
        val turnaroundTimes = IntArray(n)

        for (i in 0 until n) {
            turnaroundTimes[i] = waitingTimes[i] + burstTimes[i]
        }

        return turnaroundTimes
    }

    private fun displayResults(processOrder: IntArray, waitingTimes: IntArray, turnaroundTimes: IntArray) {
        // Clear previous results
        tableLayoutResult.removeAllViews()

        // Display the results in a table format
        val headerRow = TableRow(this)
        addTextViewToRow(headerRow, "Process")
        addTextViewToRow(headerRow, "Burst Time")
        addTextViewToRow(headerRow, "Arrival Time")
        addTextViewToRow(headerRow, "Waiting Time")
        addTextViewToRow(headerRow, "Turnaround Time")
        tableLayoutResult.addView(headerRow)

        for (i in processOrder.indices) {
            val resultRow = TableRow(this)
            addTextViewToRow(resultRow, "P${processOrder[i] + 1}")
            addTextViewToRow(resultRow, burstTimes[processOrder[i]].toString())
            addTextViewToRow(resultRow, arrivalTimes[processOrder[i]].toString())
            addTextViewToRow(resultRow, waitingTimes[i].toString())
            addTextViewToRow(resultRow, turnaroundTimes[i].toString())
            tableLayoutResult.addView(resultRow)
        }
    }

    private fun addTextViewToRow(row: TableRow, text: String) {
        val textView = TextView(this)
        textView.text = text
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        row.addView(textView)
    }


    // Extension function to convert String to Editable
    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
