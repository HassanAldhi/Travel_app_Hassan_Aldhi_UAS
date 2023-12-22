package com.example.travelapp.authentication
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.travelapp.user.UserMainActivity
import com.example.travelapp.admin.DashboardActivity
import com.example.travelapp.databinding.FragmentLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        prefManager = PrefManager.getInstance(requireContext())

        with(binding) {
            btnLogin.setOnClickListener {
                val enteredEmail = edtEmail.text.toString().trim()
                val enteredPassword = edtPassword.text.toString().trim()

                if (enteredEmail.isNotEmpty() && enteredPassword.isNotEmpty()) {
                    val usersCollection = firestore.collection("users")

                    usersCollection.whereEqualTo("email", enteredEmail).get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val userDocument = querySnapshot.documents[0]
                                val storedPassword = userDocument.getString("password")
                                val userRole = userDocument.getString("role")
                                val userId = userDocument.id
                                val username = userDocument.getString("username")
                                val email = userDocument.getString("email")
                                val phone = userDocument.getString("phone")

                                if (enteredPassword == storedPassword) {
                                    // Authentication successful, check user role
                                    prefManager.setLoggedIn(true)
                                    prefManager.saveId(userId)
                                    if (username != null ) {
                                        prefManager.saveUsername(username)
                                    }
                                    if (email != null) {
                                        prefManager.saveEmail(email)
                                    }
                                    if (phone != null) {
                                        prefManager.savePhone(phone)
                                    }
                                    prefManager.saveRole(userRole ?: "")

                                    when (userRole) {
                                        "admin" -> {
                                            val intent = Intent(requireContext(), DashboardActivity::class.java)
                                            showToast("Login success as Admin!")
                                            startActivity(intent)
                                        }
                                        "user" -> {
                                            val intent = Intent(requireContext(), UserMainActivity::class.java)
                                            showToast("Login success")
                                            startActivity(intent)
                                        }
                                    }
                                    setEmpty()
                                } else {
                                    // password salah
                                    showToast("Incorrect password")
                                }
                            } else {
                                // user dengan email yang dimasukkan tidak ditemukan
                                showToast("User not found")
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Tangani kegagalan mengambil data pengguna
                            showToast("Error: $exception")
                        }
                } else {
                    // Nama pengguna dan kata sandi dibutuhkan
                    showToast("Username and password are required")
                }
            }
        }

        binding.txtRegister.setOnClickListener{
            (activity as AuthActivity).viewPager2.setCurrentItem(1)
        }

        return view
    }
    private fun showToast(message: String) {
        val context: Context? = activity
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun setEmpty() {
        binding.edtEmail.setText("")
        binding.edtPassword.setText("")
    }
}
