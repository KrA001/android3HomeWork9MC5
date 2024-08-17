package com.example.android3homework9mc5.ui.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.android3homework9mc5.R
import com.example.android3homework9mc5.databinding.FragmentHomeBinding
import com.example.android3homework9mc5.ui.adapters.CatalogAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.ren.onlinestore.models.Product
import com.ren.onlinestore.utils.UIState
import com.ren.onlinestore.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private var catalogAdapter: CatalogAdapter? = CatalogAdapter(this::OnCLick)
    private val db = Firebase.firestore

    private fun OnCLick(product: Product) {
            // Создаем HashMap с данными продукта
            val productData = hashMapOf(
                "image" to product.image,
                "price" to product.price,
                "firm" to product.firm,
                "description" to product.description
            )

            // Добавляем данные в Firestore
            db.collection("userFavorites")
                .add(productData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "nah you need to try again", Toast.LENGTH_SHORT).show()
                    }
                }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        showProducts(view)
    }

    private fun setupRecyclerView() {
        catalogAdapter?.let {
            binding.rvCatalog.adapter = it
        }

    }

    private fun showProducts(view: View) {
        viewModel.productsState.observe(viewLifecycleOwner) { uiState ->
            uiState?.let {
                when (it) {
                    is UIState.Error -> {
                        Log.e("products", it.error.message)
                        view.showSnackbar(it.message, Snackbar.LENGTH_INDEFINITE)
                    }
                    UIState.Loading -> {}
                    is UIState.Success -> catalogAdapter?.submitList(it.data)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        catalogAdapter = null
        _binding = null
    }
}