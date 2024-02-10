package com.example.pizzaorderapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var sizeCost: Double = 0.0
    private var toppingsCost: DoubleArray = DoubleArray(6)
    private var spicyCost: Double = 0.0
    private var quantity: Int = 1
    private var deliveryCost: Double = 0.0
    private var subtotalCost: Double = 0.0
    private var calculatedTax: Double = 0.0
    private var totalCost: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Pizza Size Spinner
        val sizeList = listOf("Choose a pizza type", "Medium", "Large", "Extra Large", "Party Size")
        val sizeAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sizeList)
        val sizeSpinner = findViewById<Spinner>(R.id.size_spinner)

        sizeSpinner.adapter = sizeAdapter
        sizeSpinner.onItemSelectedListener = this

        // Spiciness Level
        val spicyLevelSeekBarLabel = findViewById<TextView>(R.id.spicy_level_text)
        findViewById<SeekBar>(R.id.spicy_seekBar).setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
               spicyLevelSeekBarLabel.text = "Spiciness Level: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //
            }

        })



    }

    fun pizzaTypeRadioButtonClick(view: View) {
        val selectedPizzaImage = when (view.id) {
            R.id.pepperoni_rbutton -> R.drawable.pepperoni
            R.id.bbq_chicken_rbutton -> R.drawable.bbq_chicken
            R.id.margherita_rbutton -> R.drawable.margheritta
            else -> R.drawable.hawaiian
        }
        findViewById<ImageView>(R.id.pizzaImage).setImageResource(selectedPizzaImage)
        updateCost()
    }

    fun toppingCheckBoxesClick(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
            when (view.id) {
                R.id.tomatoes_chbox -> {
                    if (checked)
                        toppingsCost[0] = 1.0
                    else
                        toppingsCost[0] = 0.0
                }
                R.id.onion_chbox -> {
                    if (checked)
                        toppingsCost[1] = 1.25
                    else
                        toppingsCost[1] = 0.0
                }
                R.id.mushrooms_chbox -> {
                    if (checked)
                        toppingsCost[2] = 2.3
                    else
                        toppingsCost[2] = 0.0
                }
                R.id.olives_chbox -> {
                    if (checked)
                        toppingsCost[3] = 1.7
                    else
                        toppingsCost[3] = 0.0
                }
                R.id.brocoli_chbox -> {
                    if (checked)
                        toppingsCost[4] = 1.8
                    else
                        toppingsCost[4] = 0.0
                }
                else -> {
                    if (checked)
                        toppingsCost[5] = 2.0
                    else
                        toppingsCost[5] = 0.0
                }
            }
        }
        updateCost()
    }

    fun spicySwitchClick(view: View) {
        val spicySwitch = findViewById<Switch>(R.id.spicy_switch)
        val spicyLevelText = findViewById<TextView>(R.id.spicy_level_text)

        var switchText: String
        if (spicySwitch.isChecked) {
            spicyCost = 1.0
            switchText = "Yes, $1.00"
            spicyLevelText.visibility = View.VISIBLE
        } else {
            spicyCost = 0.0
            switchText = "No, $0.00"
            spicyLevelText.visibility = View.INVISIBLE
        }
        spicySwitch.text = switchText
        updateCost()
    }

    fun deliverySwitchClick(view: View) {
        val deliverySwitch = findViewById<Switch>(R.id.delivery_switch)
        var switchText: String
        if (deliverySwitch.isChecked) {
            deliveryCost = 2.0
            switchText = "Yes, $2.00"
        } else {
            deliveryCost = 0.0
            switchText = "No, $0.00"
        }
        deliverySwitch.text = switchText
        updateCost()
    }

    fun quantityButtonClick(view: View) {
        val plusButton = findViewById<Button>(R.id.plus_button)
        val minusButton = findViewById<Button>(R.id.minus_button)
        val quantityTV = findViewById<TextView>(R.id.quantity_num_text)

        if (view == plusButton) {
            quantity ++
        } else if (view == minusButton && quantity >1) {
            quantity --
        }
        if (quantity == 1) {
            minusButton.isEnabled = false
            minusButton.background.setTint(Color.parseColor("#808080"))
        } else {
            minusButton.isEnabled = true
            minusButton.background.setTint(Color.parseColor("#F34336"))
        }
        quantityTV.text = quantity.toString()
        updateCost()
    }

    private fun updateCost() {
        val subtotalTV = findViewById<TextView>(R.id.subtotal_num_text)
        val taxTV = findViewById<TextView>(R.id.tax_num_text)
        val totalTV = findViewById<TextView>(R.id.total_num_text)

        calculateSubtotal()
        calculateTax()
        calculateTotal()
        subtotalTV.text = String.format("$%.2f", subtotalCost)
        taxTV.text = String.format("$%.2f",calculatedTax)
        totalTV.text = String.format("$%.2f",totalCost)

    }
    fun resetButtonClick(view: View) {
        // Reset Variables and recalculate subtotal, tax and total
        sizeCost = 0.0
        for (i in 0..5)
            toppingsCost[i] = 0.0
        spicyCost = 0.0
        quantity = 1
        deliveryCost = 0.0
        updateCost()

        // Reset Pizza Type and get pizza crust image
        findViewById<RadioGroup>(R.id.pizza_radio_group).clearCheck()
        findViewById<ImageView>(R.id.pizzaImage).setImageResource(R.drawable.pizza_crust)

        // Reset pizza size
        findViewById<Spinner>(R.id.size_spinner).setSelection(0)

        // Reset topping selections
        findViewById<CheckBox>(R.id.tomatoes_chbox).isChecked = false
        findViewById<CheckBox>(R.id.mushrooms_chbox).isChecked = false
        findViewById<CheckBox>(R.id.olives_chbox).isChecked = false
        findViewById<CheckBox>(R.id.onion_chbox).isChecked = false
        findViewById<CheckBox>(R.id.brocoli_chbox).isChecked = false
        findViewById<CheckBox>(R.id.spinach_chbox).isChecked = false

        // Reset spicy related widgets
        val spicySwitch = findViewById<Switch>(R.id.spicy_switch)
        val spicyLevelText = findViewById<TextView>(R.id.spicy_level_text)
        spicySwitch.isChecked = false
        spicySwitch.text = "No, $0.00"
        spicyLevelText.visibility = View.INVISIBLE
        spicyLevelText.text = "Spiciness Level: 1"
        findViewById<SeekBar>(R.id.spicy_seekBar).progress = 0

        // Reset Quantity
        findViewById<Button>(R.id.minus_button).isEnabled = false
        findViewById<Button>(R.id.minus_button).background.setTint(Color.parseColor("#808080"))
        findViewById<TextView>(R.id.quantity_num_text).text = "1"

        // Reset Delivery Switch
        val deliverySwitch = findViewById<Switch>(R.id.delivery_switch)
        deliverySwitch.isChecked = false
        deliverySwitch.text = "No, $0.00"
    }
    private fun calculateSubtotal() {
        var totalToppingsCost : Double = 0.0
        for (i in 0..5)
            totalToppingsCost+=toppingsCost[i]
        subtotalCost = (sizeCost + totalToppingsCost + spicyCost) * quantity
    }
    private fun calculateTax() {
        calculatedTax = subtotalCost * 6.35 / 100
    }
    private fun calculateTotal() {
        totalCost = subtotalCost + calculatedTax + deliveryCost
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedSize = parent?.getItemAtPosition(position).toString()
        //if (position > 0)
        //    Toast.makeText(this, "You selected $selectedSize!", Toast.LENGTH_SHORT).show()
        sizeCost = when(position) {
            0 -> 0.0
            1 -> 9.99
            2 -> 13.99
            3 -> 15.99
            else -> 25.99
        }
        updateCost()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //
    }
}