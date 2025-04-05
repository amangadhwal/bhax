package com.bhax.app.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.bhax.app.databinding.FragmentEditorBinding
import com.bhax.app.ui.theme.ThemeManager

class EditorFragment : Fragment() {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!
    private lateinit var themeManager: ThemeManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        themeManager = ThemeManager.getInstance(requireContext())
        setupThemeSwitch()
    }

    private fun setupThemeSwitch() {
        binding.themeSwitch.apply {
            isChecked = themeManager.isAuroraTheme()
            updateSwitchLabel()
            
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != themeManager.isAuroraTheme()) {
                    (activity as? AppCompatActivity)?.let { activity ->
                        themeManager.toggleTheme(activity)
                    }
                    updateSwitchLabel()
                }
            }
        }
    }

    private fun updateSwitchLabel() {
        val nextTheme = if (themeManager.isAuroraTheme()) {
            getString(R.string.theme_saanjh)
        } else {
            getString(R.string.theme_aurora)
        }
        binding.themeSwitch.text = getString(R.string.theme_switch_label, nextTheme)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}