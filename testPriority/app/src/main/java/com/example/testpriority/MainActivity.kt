package com.example.testpriority

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextProcesses: EditText
    private lateinit var buttonCalculate: Button
    private lateinit var tableLayoutResult: TableLayout

    private lateinit var burstTimes: IntArray
    private lateinit var arrivalTimes: IntArray
    private lateinit var priorities: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextProcesses = findViewById(R.id.editTextProcesses)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        tableLayoutResult = findViewById(R.id.tableLayoutResult)

        buttonCalculate.setOnClickListener {
            calculatePriorityScheduling()
        }
    }

    private fun calculatePriorityScheduling() {
        // Clear previous results
        tableLayoutResult.removeAllViews()

        val n = editTextProcesses.text.toString().toInt()

        if (n <= 0) {
            return
        }

        burstTimes = IntArray(n)
        arrivalTimes = IntArray(n)
        priorities = IntArray(n)

        // Dynamically create EditTexts for burst times, arrival times, and priorities
        for (i in 0 until n) {
            val editTextBurstTime = EditText(this)
            editTextBurstTime.hint = "Burst Time for Process ${i + 1}"
            editTextBurstTime.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            tableLayoutResult.addView(editTextBurstTime)

            val editTextArrivalTime = EditText(this)
            editTextArrivalTime.hint = "Arrival Time for Process ${i + 1}"
            editTextArrivalTime.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            tableLayoutResult.addView(editTextArrivalTime)

            val editTextPriority = EditText(this)
            editTextPriority.hint = "Priority for Process ${i + 1}"
            editTextPriority.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            tableLayoutResult.addView(editTextPriority)

            burstTimes[i] = 0
            arrivalTimes[i] = 0
            priorities[i] = 0
        }

        val calculateButton = Button(this)
        calculateButton.text = "Calculate"
        calculateButton.setOnClickListener {
            // Update burstTimes, arrivalTimes, and priorities arrays with values from EditText elements
            for (i in 0 until n) {
                burstTimes[i] = (tableLayoutResult.getChildAt(i * 3) as EditText).text.toString().toInt()
                arrivalTimes[i] = (tableLayoutResult.getChildAt(i * 3 + 1) as EditText).text.toString().toInt()
                priorities[i] = (tableLayoutResult.getChildAt(i * 3 + 2) as EditText).text.toString().toInt()
            }

            val processOrder = calculatePriorityOrder()
            val waitingTimes = calculateWaitingTime(processOrder)
            val turnaroundTimes = calculateTurnaroundTime(waitingTimes)

            displayResults(processOrder, waitingTimes, turnaroundTimes)
        }

        tableLayoutResult.addView(calculateButton)
    }

    private fun calculatePriorityOrder(): IntArray {
        val n = burstTimes.size
        val processOrder = IntArray(n) { it }

        // Sort processes based on priority using Priority Scheduling algorithm
        processOrder.sortedWith(compareBy { priorities[it] })

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
        addTextViewToRow(headerRow, "Priority")
        addTextViewToRow(headerRow, "Waiting Time")
        addTextViewToRow(headerRow, "Turnaround Time")
        tableLayoutResult.addView(headerRow)

        for (i in processOrder.indices) {
            val resultRow = TableRow(this)
            addTextViewToRow(resultRow, "P${processOrder[i] + 1}")
            addTextViewToRow(resultRow, burstTimes[processOrder[i]].toString())
            addTextViewToRow(resultRow, arrivalTimes[processOrder[i]].toString())
            addTextViewToRow(resultRow, priorities[processOrder[i]].toString())
            addTextViewToRow(resultRow, waitingTimes[i].toString())
            addTextViewToRow(resultRow, turnaroundTimes[i].toString())
            tableLayoutResult.addView(resultRow)
        }
    }

    private fun addTextViewToRow(row: TableRow, text: String) {
        val textView = EditText(this)
        textView.text = text.toEditable()
        textView.isEnabled = false
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        row.addView(textView)
    }

    // Extension function to convert String to Editable
    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
