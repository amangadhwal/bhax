package com.bhax.app.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.bhax.app.data.UserAgreementManager
import com.bhax.app.databinding.DialogUserAgreementBinding

class UserAgreementDialog(
    context: Context,
    private val onAgreeClick: (String) -> Unit,
    private val onExploreClick: () -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogUserAgreementBinding
    private val agreementManager = UserAgreementManager.getInstance(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogUserAgreementBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        setupClickListeners()
        setCanceledOnTouchOutside(false)
    }

    private fun setupClickListeners() {
        binding.btnAgree.setOnClickListener {
            agreementManager.agreeAndGenerateToken()
            val token = agreementManager.getToken()
            token?.let { onAgreeClick(it) }
            dismiss()
        }

        binding.btnExplore.setOnClickListener {
            onExploreClick()
            dismiss()
        }
    }

    companion object {
        fun show(
            context: Context,
            onAgreeClick: (String) -> Unit,
            onExploreClick: () -> Unit
        ) {
            UserAgreementDialog(context, onAgreeClick, onExploreClick).show()
        }
    }
}
package com.bhax.app.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.bhax.app.data.UserAgreementManager
import com.bhax.app.databinding.DialogUserAgreementBinding

class UserAgreementDialog(
    context: Context,
    private val onAgreeClick: (String) -> Unit,
    private val onExploreClick: () -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogUserAgreementBinding
    private val agreementManager = UserAgreementManager.getInstance(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogUserAgreementBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        setupClickListeners()
        setCanceledOnTouchOutside(false)
    }

    private fun setupClickListeners() {
        binding.btnAgree.setOnClickListener {
            agreementManager.agreeAndGenerateToken()
            val token = agreementManager.getToken()
            token?.let { onAgreeClick(it) }
            dismiss()
        }

        binding.btnExplore.setOnClickListener {
            onExploreClick()
            dismiss()
        }
    }

    companion object {
        fun show(
            context: Context,
            onAgreeClick: (String) -> Unit,
            onExploreClick: () -> Unit
        ) {
            UserAgreementDialog(context, onAgreeClick, onExploreClick).show()
        }
    }
}