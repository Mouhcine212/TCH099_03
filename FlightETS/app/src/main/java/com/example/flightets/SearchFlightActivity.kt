package com.example.flightets

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SearchFlightActivity : AppCompatActivity() {

    private lateinit var destinationInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var searchButton: Button
    private lateinit var resultsRecyclerView: RecyclerView
    private lateinit var noResultsText: TextView

    private val flightAdapter = FlightAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_flight)

        destinationInput = findViewById(R.id.destinationInput)
        dateInput = findViewById(R.id.dateInput)
        searchButton = findViewById(R.id.searchButton)
        resultsRecyclerView = findViewById(R.id.recyclerView)
        noResultsText = findViewById(R.id.noResultsText)

        resultsRecyclerView.layoutManager = LinearLayoutManager(this)
        resultsRecyclerView.adapter = flightAdapter

        // Show DatePickerDialog when clicking dateInput
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    dateInput.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        searchButton.setOnClickListener {
            val destination = destinationInput.text.toString()
            val date = dateInput.text.toString()

            if (destination.isBlank() || date.isBlank()) {
                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                searchFlights(destination, date)
            }
        }
    }

    private fun searchFlights(destination: String, date: String) {
        val request = SearchRequest(destination, date)

        RetrofitClient.instance.searchFlights(request).enqueue(object : Callback<List<Flight>> {
            override fun onResponse(call: Call<List<Flight>>, response: Response<List<Flight>>) {
                if (response.isSuccessful && response.body() != null) {
                    val flights = response.body()!!
                    if (flights.isEmpty()) {
                        noResultsText.visibility = TextView.VISIBLE
                        flightAdapter.setFlights(emptyList())
                    } else {
                        noResultsText.visibility = TextView.GONE
                        flightAdapter.setFlights(flights)
                    }
                } else {
                    Toast.makeText(
                        this@SearchFlightActivity,
                        "Erreur lors de la recherche",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Flight>>, t: Throwable) {
                Toast.makeText(
                    this@SearchFlightActivity,
                    "Erreur: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
