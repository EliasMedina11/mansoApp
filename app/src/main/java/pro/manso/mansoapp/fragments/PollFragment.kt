package pro.manso.mansoapp.fragments


import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import pro.manso.mansoapp.models.Vote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.fragment_rates.*
import kotlinx.android.synthetic.main.fragment_rates.view.*
import pro.manso.mansoapp.*
import pro.manso.mansoapp.BuildConfig
import pro.manso.mansoapp.R
import pro.manso.mansoapp.models.CommandEvent
import pro.manso.mansoapp.models.NewVoteEvent
import pro.manso.mansoapp.utils.RxBus
import pro.manso.mansoapp.votes.VotesActivity
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList

class PollFragment : Fragment() {

    private lateinit var _view: View

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var voteDBRef: CollectionReference
    private val voteList: ArrayList<Vote> = ArrayList()
    private var voteSubscription: ListenerRegistration? = null
    private lateinit var adminDBRef: CollectionReference
    private var voted = false

    private val timer = CountDownTimerClass(10000, 1000)



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_rates, container, false)

        setUpVoteDB()
        setUpCurrentUser()

        setUpButton1()
        setUpButton2()
        setUpButton3()
        setUpButton4()
        setUpButton5()
        setUpButton6()

        subscribeToVotes()
        votacionActivada()
        limpiarVotos()

        //   adminCommandVotesStart()
       // observeRates()

        return _view
    }

    private fun votacionActivada(){
        RxBus.listen(CommandEvent::class.java ).subscribe {
            if (it.command){
                activity!!.toast("Votacion activada!")
                vote_buttons_layout.visibility = View.VISIBLE
                loading.visibility = View.GONE
            } else {
                activity!!.toast("Votacion Desactivada :(")
                vote_buttons_layout.visibility = View.INVISIBLE
                loading.visibility = View.VISIBLE
            }
        }
    }

    private fun limpiarVotos() {
        RxBus.listen(CommandEvent::class.java).subscribe{
            if (it.cleanVote) {
                limpiezadeVotos(true)
            }
        }
    }

    private fun limpiezadeVotos(vot: Boolean){
        if (vot) {
            voted = false
            vote_buttons_layout.visibility = View.INVISIBLE
            loading.visibility = View.VISIBLE
        }
    }

   /* private fun setOnClickListeners(view: View) {
        view.setOnClickListener {
            when(id) {
                R.id.buttonOption1 -> {
                     vote = Vote(currentUser.uid, "1", Date(), currentUser.email!!)
                    displayAlert("Tu voto es: 1. ¿Es esto correcto?", vote!!)
                }
                R.id.buttonOption2 -> {
                     vote = Vote(currentUser.uid, "2", Date(), currentUser.email!!)
                    displayAlert("Tu voto es: 2. ¿Es esto correcto?", vote!!)
                }
                R.id.buttonOption3 -> {
                     vote = Vote(currentUser.uid, "3", Date(), currentUser.email!!)
                    displayAlert("Tu voto es: 3. ¿Es esto correcto?", vote!!)
                }
                R.id.buttonOption4 -> {
                     vote = Vote(currentUser.uid, "4", Date(), currentUser.email!!)
                    displayAlert("Tu voto es: 4. ¿Es esto correcto?", vote!!)
                }
                R.id.buttonOption5 -> {
                     vote = Vote(currentUser.uid, "5", Date(), currentUser.email!!)
                    displayAlert("Tu voto es: 5. ¿Es esto correcto?", vote!!)
                }
                R.id.buttonOption6 -> {
                     vote = Vote(currentUser.uid, "6", Date(), currentUser.email!!)
                    displayAlert("Tu voto es: 6. ¿Es esto correcto?", vote!!)
                }
            }
        }
    }
*/

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

    private fun subscribeToVotes (){
        voteSubscription =  voteDBRef
                .orderBy("userId",Query.Direction.DESCENDING)
                .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {

                    override fun onEvent(snapShot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                        exception?.let {
                            activity!!.toast("Exception")
                            return
                        }
                        snapShot?.let {
                            voteList.clear()
                            val newVotes = it.toObjects(Vote::class.java)
                            voteList.addAll(newVotes.asReversed())
                                RxBus.publish(NewVoteEvent(voteList,voteList.size))
                        }
                    }
                })

    }

     private fun displayAlert(title: String, vote: Vote) {
        val alert = AlertDialog.Builder(this.context)
        with(alert) {
            setTitle(title)

            setPositiveButton("Si") { _, _ ->
                saveVoteInDB(vote)
                timer.start()
                voted = true
                limpiezadeVotos(voted)
                activity!!.goToActivity<VotesActivity> {  }
            }

            setNegativeButton("NO") { _, _ ->
            }
        }
        // Dialog
        val dialog = alert.create()
        dialog.show()
    }

    inner class CountDownTimerClass(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
          //  setViewAndChildrenEnabled(fragment_rates,true)
            _view.buttonOption1.setBackgroundResource(R.drawable.vote_button_ripple)
            _view.buttonOption2.setBackgroundResource(R.drawable.vote_button_ripple)
            _view.buttonOption3.setBackgroundResource(R.drawable.vote_button_ripple)
            _view.buttonOption4.setBackgroundResource(R.drawable.vote_button_ripple)
            _view.buttonOption5.setBackgroundResource(R.drawable.vote_button_ripple)
            _view.buttonOption6.setBackgroundResource(R.drawable.vote_button_ripple)
        }

        override fun onTick(p0: Long) {
          //  setViewAndChildrenEnabled(fragment_rates,false)
            _view.buttonOption1.setBackgroundResource(R.drawable.vote_button_ripple_disabled)
            _view.buttonOption2.setBackgroundResource(R.drawable.vote_button_ripple_disabled)
            _view.buttonOption3.setBackgroundResource(R.drawable.vote_button_ripple_disabled)
            _view.buttonOption4.setBackgroundResource(R.drawable.vote_button_ripple_disabled)
            _view.buttonOption5.setBackgroundResource(R.drawable.vote_button_ripple_disabled)
            _view.buttonOption6.setBackgroundResource(R.drawable.vote_button_ripple_disabled)
        }

    }

    private fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                setViewAndChildrenEnabled(child, enabled)
            }
        }
    }


/*
    private fun adminCommandVotesStart() {
        store.collection("admin")
                .whereEqualTo("command","start")
                .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    activity!!.toast("Exception!")
                    return
                }
                if (querySnapshot != null) {
                    for (dc: DocumentChange in querySnapshot.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            _view.imageViewWaiting.visibility = View.GONE
                        } else {
                            _view.imageViewWaiting.visibility = View.VISIBLE
                        }
                    }
                }
            }

        })
    }
*/
}
