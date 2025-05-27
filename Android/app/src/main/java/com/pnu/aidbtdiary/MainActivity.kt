package com.pnu.aidbtdiary

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pnu.aidbtdiary.ml.TextClassificationHelper
import org.tensorflow.lite.support.label.Category

class MainActivity : AppCompatActivity(), TextClassificationHelper.TextResultsListener {
    
    private lateinit var textClassificationHelper: TextClassificationHelper
    
    // UI 요소들
    private lateinit var modelSpinner: Spinner
    private lateinit var inputText: EditText
    private lateinit var classifyButton: Button
    private lateinit var resultText: TextView
    private lateinit var inferenceTimeText: TextView
    
    // 모델 옵션
    private val modelOptions = arrayOf("Word Vector", "MobileBERT")
    private val modelFiles = arrayOf(
        TextClassificationHelper.WORD_VEC,
        TextClassificationHelper.MOBILEBERT
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initViews()
        setupSpinner()
        initTextClassificationHelper()
        setupClickListeners()
    }
    
    private fun initViews() {
        modelSpinner = findViewById(R.id.modelSpinner)
        inputText = findViewById(R.id.inputText)
        classifyButton = findViewById(R.id.classifyButton)
        resultText = findViewById(R.id.resultText)
        inferenceTimeText = findViewById(R.id.inferenceTimeText)
    }
    
    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modelOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        modelSpinner.adapter = adapter
        
        modelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedModel = modelFiles[position]
                textClassificationHelper.currentModel = selectedModel
                textClassificationHelper.initClassifier()
                
                resultText.text = "모델이 변경되었습니다: ${modelOptions[position]}"
                inferenceTimeText.text = "추론 시간: -"
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    
    private fun initTextClassificationHelper() {
        textClassificationHelper = TextClassificationHelper(
            currentDelegate = TextClassificationHelper.DELEGATE_CPU,
            currentModel = TextClassificationHelper.WORD_VEC,
            context = this,
            listener = this
        )
    }
    
    private fun setupClickListeners() {
        classifyButton.setOnClickListener {
            val text = inputText.text.toString().trim()
            
            if (text.isEmpty()) {
                Toast.makeText(this, "텍스트를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // 버튼 비활성화 및 로딩 상태 표시
            classifyButton.isEnabled = false
            classifyButton.text = "분류 중..."
            resultText.text = "분류를 진행하고 있습니다..."
            inferenceTimeText.text = "추론 시간: -"
            
            // 텍스트 분류 실행
            textClassificationHelper.classify(text)
        }
    }
    
    // TextClassificationHelper.TextResultsListener 구현
    override fun onResult(results: List<Category>, inferenceTime: Long) {
        runOnUiThread {
            // 버튼 상태 복원
            classifyButton.isEnabled = true
            classifyButton.text = "감정 분류 실행"
            
            // 결과 표시
            if (results.isNotEmpty()) {
                val resultBuilder = StringBuilder()
                resultBuilder.append("분류 결과:\n\n")
                
                results.forEachIndexed { index, category ->
                    val confidence = (category.score * 100).toInt()
                    resultBuilder.append("${index + 1}. ${category.label}: ${confidence}%\n")
                }
                
                // 가장 높은 확률의 감정 강조
                val topResult = results[0]
                val topConfidence = (topResult.score * 100).toInt()
                resultBuilder.append("\n🎯 예측된 감정: ${topResult.label} (${topConfidence}%)")
                
                resultText.text = resultBuilder.toString()
            } else {
                resultText.text = "분류 결과를 가져올 수 없습니다."
            }
            
            // 추론 시간 표시
            inferenceTimeText.text = "추론 시간: ${inferenceTime}ms"
        }
    }
    
    override fun onError(error: String) {
        runOnUiThread {
            // 버튼 상태 복원
            classifyButton.isEnabled = true
            classifyButton.text = "감정 분류 실행"
            
            // 에러 메시지 표시
            resultText.text = "오류가 발생했습니다: $error"
            inferenceTimeText.text = "추론 시간: -"
            
            Toast.makeText(this, "분류 중 오류가 발생했습니다: $error", Toast.LENGTH_LONG).show()
        }
    }
}
