package pro.manso.mansoapp.fragments


import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import pro.manso.mansoapp.longToast
import pro.manso.mansoapp.models.Vote
import pro.manso.mansoapp.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_rates.view.*
import pro.manso.mansoapp.R
import java.util.*

class PollFragment : Fragment() {

    private lateinit var _view: View

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var voteDBRef: CollectionReference

    private val timer = countDownTimer(10000, 1000)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_rates, container, false)

        _view.invalidate()

        setUpVoteDB()
        setUpCurrentUser()
        setUpButton1()
        setUpButton2()
        setUpButton3()
        setUpButton4()
        setUpButton5()
        setUpButton6()


        /*** Refactor This Code ***/
    /*    _view.setOnClickListener {
            View.OnClickListener { v ->
                when (v.id) {
                    R.id.buttonOption1 -> {
                        val vote = Vote(currentUser.uid, "1", Date())
                        displayAlert("Tu voto es: 1. ¿Es esto correcto?", vote)
                    }
                    R.id.buttonOption2 -> {
                        val vote = Vote(currentUser.uid, "2", Date())
                        displayAlert("Tu voto es: 2. ¿Es esto correcto?", vote)
                    }
                    R.id.buttonOption3 -> {
                        val vote = Vote(currentUser.uid, "3", Date())
                        displayAlert("Tu voto es: 3. ¿Es esto correcto?", vote)
                    }
                    R.id.buttonOption4 -> {
                        val vote = Vote(currentUser.uid, "4", Date())
                        displayAlert("Tu voto es: 4. ¿Es esto correcto?", vote)
                    }
                    R.id.buttonOption5 -> {
                        val vote = Vote(currentUser.uid, "5", Date())
                        displayAlert("Tu voto es: 5. ¿Es esto correcto?", vote)
                    }
                    R.id.buttonOption6 -> {
                        val vote = Vote(currentUser.uid, "6", Date())
                        displayAlert("Tu voto es: 6. ¿Es esto correcto?", vote)
                    }
                }
            }
        }
        */


        return _view
    }

    private fun setUpButton6() {
        _view.buttonOption6.setOnClickListener {
            val vote = Vote(currentUser.uid, "6", Date(), currentUser.email!!)
            displayAlert("Tu voto es: 6. ¿Es esto correcto?", vote)
        }
    }

    private fun setUpButton5() {
        _view.buttonOption5.setOnClickListener {
            val vote = Vote(currentUser.uid, "5", Date(), currentUser.email!!)
            displayAlert("Tu voto es: 5. ¿Es esto correcto?", vote)
        }
    }

    private fun setUpButton4() {
        _view.buttonOption4.setOnClickListener {
            val vote = Vote(currentUser.uid, "4", Date(), currentUser.email!!)
            displayAlert("Tu voto es: 4. ¿Es esto correcto?", vote)
        }
    }

    private fun setUpButton3() {
       _view.buttonOption3.setOnClickListener {
            val vote = Vote(currentUser.uid, "3", Date(),  currentUser.email!!)
            displayAlert("Tu voto es: 3. ¿Es esto correcto?", vote)
        }
    }

    private fun setUpButton2() {
        _view.buttonOption2.setOnClickListener {
            val vote = Vote(currentUser.uid, "2", Date(), currentUser.email!!)
            displayAlert("Tu voto es: 2. ¿Es esto correcto?", vote)
        }
    }

    private fun setUpButton1() {
        _view.buttonOption1.setOnClickListener {
            val vote = Vote(currentUser.uid, "1", Date(), currentUser.email!!)
            displayAlert("Tu voto es: 1. ¿Es esto correcto?", vote)
        }
    }

    private fun saveVoteInDB(vote: Vote) {
        val newVote = HashMap<String, Any>()
        newVote["userId"] = vote.userId
        newVote["vote"] = vote.vote
        newVote["sentAt"] = vote.sentAt
        newVote ["userEmail"] = vote.userEmail

        voteDBRef.add(newVote)
                .addOnFailureListener {
                    activity!!.toast("Error, intenta nuevamente")
                }
                .addOnCompleteListener {
                    activity!!.longToast("Tu voto ha sido guardado, Recuerda que solamente puedes votar 1 sola vez!")
                }
    }

    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpVoteDB() {
        voteDBRef = store.collection("votos")

    }



    fun displayAlert(title: String, vote: Vote) {
        val alert = AlertDialog.Builder(this.context)
        with(alert) {
            setTitle(title)

            setPositiveButton("Si") { dialog, whichButton ->
                saveVoteInDB(vote)
                timer.start()
            }

            setNegativeButton("NO") { dialog, whichButton ->
            }
        }

        // Dialog
        val dialog = alert.create()
        dialog.show()
    }

    inner class countDownTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {

            _view.buttonOption1.isEnabled = true
            _view.buttonOption2.isEnabled = true
            _view.buttonOption3.isEnabled = true
            _view.buttonOption4.isEnabled = true
            _view.buttonOption5.isEnabled = true
            _view.buttonOption6.isEnabled = true
            _view.buttonOption1.setBackgroundResource(R.drawable.vote_button_ripple)
            _view.buttonOption2.setBackgroundResource(R.drawable.vote_button_ripple)
            _view.buttonOption3.setBackgroundResource(R.drawable.vote_button_ripple)
            _view.buttonOption4.setBackgroundResource(R.drawable.vote_button_ripple)
            _view.buttonOption5.setBackgroundResource(R.drawable.vote_button_ripple)
            _view.buttonOption6.setBackgroundResource(R.drawable.vote_button_ripple)
        }

        override fun onTick(p0: Long) {
            _view.buttonOption1.isEnabled = false
            _view.buttonOption2.isEnabled = false
            _view.buttonOption3.isEnabled = false
            _view.buttonOption4.isEnabled = false
            _view.buttonOption5.isEnabled = false
            _view.buttonOption6.isEnabled = false
            _view.buttonOption1.setBackgroundResource(R.drawable.vote_button_ripple_disabled)
            _view.buttonOption2.setBackgroundResource(R.drawable.vote_button_ripple_disabled)
            _view.buttonOption3.setBackgroundResource(R.drawable.vote_button_ripple_disabled)
            _view.buttonOption4.setBackgroundResource(R.drawable.vote_button_ripple_disabled)
            _view.buttonOption5.setBackgroundResource(R.drawable.vote_button_ripple_disabled)
            _view.buttonOption6.setBackgroundResource(R.drawable.vote_button_ripple_disabled)

        }

    }

}
