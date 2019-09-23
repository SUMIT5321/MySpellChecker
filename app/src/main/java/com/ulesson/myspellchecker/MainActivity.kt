package com.ulesson.myspellchecker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.textservice.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ulesson.myspellchecker.spellCheker.SymSpellDemo
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import java.util.*

class MainActivity : AppCompatActivity(), SpellCheckerSession.SpellCheckerSessionListener {
    private var mMainView: TextView? = null
    private var mScs: SpellCheckerSession? = null
    private lateinit var symSpellDemo: SymSpellDemo

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mMainView = findViewById(R.id.main) as TextView

        doAsync {
            symSpellDemo = SymSpellDemo(3, assets)
        }

        btn_get_suggestion.setOnClickListener {
            val text = et_auto_correct.text.toString()
            doAsync {
//                mScs?.getSentenceSuggestions(
//                    //arrayOf(TextInfo(text)), 3
//                    arrayOf(TextInfo("tgisis")), 3
//                )
                val suggestionItems = symSpellDemo.lookup(text)
                runOnUiThread {
                    mMainView?.append("\n" + suggestionItems[0].term)
                }
            }
        }

        //et_auto_correct.s
    }

    public override fun onResume() {
        super.onResume()
        val tsm = getSystemService(
            Context.TEXT_SERVICES_MANAGER_SERVICE
        ) as TextServicesManager
        mScs = tsm.newSpellCheckerSession(null, Locale.ENGLISH, this, true)

//        mScs?.getSentenceSuggestions(
//            //arrayOf(TextInfo(et_auto_correct.text.toString())), 3
//            arrayOf(TextInfo("tgisis")), 3
//        )

//        if (mScs != null) {
//            // Instantiate TextInfo for each query
//            // TextInfo can be passed a sequence number and a cookie number to identify the result
//
//            // Note that getSentenceSuggestions works on JB or later.
//            Log.d(TAG, "Sentence spellchecking supported.")
//            mScs!!.getSentenceSuggestions(arrayOf(TextInfo("tgisis")), 3)
//            mScs!!.getSentenceSuggestions(
//                arrayOf(
//                    TextInfo(
//                        "I wold like to here form you"
//                    )
//                ), 3
//            )
//            mScs!!.getSentenceSuggestions(arrayOf(TextInfo("hell othere")), 3)
//
//        } else {
//            Log.e(TAG, "Couldn't obtain the spell checker service.")
//        }
    }

    public override fun onPause() {
        super.onPause()
        if (mScs != null) {
            mScs!!.close()
        }
    }

    private fun dumpSuggestionsInfoInternal(
        sb: StringBuilder, si: SuggestionsInfo, length: Int, offset: Int
    ) {
        // Returned suggestions are contained in SuggestionsInfo
        val len = si.suggestionsCount
        sb.append('\n')
        for (j in 0 until len) {
            if (j != 0) {
                sb.append(", ")
            }
            sb.append(si.getSuggestionAt(j))
        }
        sb.append(" ($len)")
        if (length != NOT_A_LENGTH) {
            sb.append(" length = $length, offset = $offset")
        }
    }

    /**
     * Callback for [SpellCheckerSession.getSuggestions]
     * and [SpellCheckerSession.getSuggestions]
     * @param results an array of [SuggestionsInfo]s.
     * These results are suggestions for [TextInfo]s queried by
     * [SpellCheckerSession.getSuggestions] or
     * [SpellCheckerSession.getSuggestions]
     */
    override fun onGetSuggestions(arg0: Array<SuggestionsInfo>) {
        Log.d(TAG, "onGetSuggestions")
        val sb = StringBuilder()
        for (i in arg0.indices) {
            dumpSuggestionsInfoInternal(sb, arg0[i], 0, NOT_A_LENGTH)
        }
        //runOnUiThread { mMainView!!.append(sb.toString()) }
    }

    /**
     * Callback for [SpellCheckerSession.getSentenceSuggestions]
     * @param results an array of [SentenceSuggestionsInfo]s.
     * These results are suggestions for [TextInfo]s
     * queried by [SpellCheckerSession.getSentenceSuggestions].
     */
    override fun onGetSentenceSuggestions(arg0: Array<SentenceSuggestionsInfo>) {
        Log.d(TAG, "onGetSentenceSuggestions")
        val sb = StringBuilder()
        for (i in arg0.indices) {
            val ssi = arg0[i]
            for (j in 0 until ssi.suggestionsCount) {
                dumpSuggestionsInfoInternal(
                    sb, ssi.getSuggestionsInfoAt(j), ssi.getOffsetAt(j), ssi.getLengthAt(j)
                )
            }
        }

        runOnUiThread { mMainView!!.append(sb.toString()) }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val NOT_A_LENGTH = -1
    }
}