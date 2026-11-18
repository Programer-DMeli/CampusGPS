package com.carbajal.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import com.carbajal.campusgps.model.Posicion
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class UbicacionRepositoryGoogle : UbicacionRepository {
    override fun playServicesDisponible(context: Context): Boolean {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        return status == ConnectionResult.SUCCESS
    }

    @SuppressLint("MissingPermission")
    override suspend fun obtenerUbicacionActual(context: Context): Posicion? {
        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val location = fusedLocationClient.lastLocation.await()
                ?: fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token
                ).await()
            location?.let { Posicion(it.latitude, it.longitude) }
        } catch (e: Exception) {
            null
        }
    }
}
