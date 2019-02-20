package pro.manso.mansoapp.votes

import android.util.Log
import com.google.firebase.firestore.*
import pro.manso.mansoapp.models.NewVoteEvent
import pro.manso.mansoapp.utils.RxBus
import java.util.EventListener

class VotesPresenter(private var view: VotesContract.View) : VotesContract.Presenter {

    var counter1 = 0
    var counter2 = 0
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun observeRates() {
        RxBus.listen(NewVoteEvent::class.java).subscribe {
            view.showLoading(false)
            counter2 = it.totalVotes
            for (i in it.voted){
                Log.e("votecap", i.vote)
                view.displayVotes(i.vote)
            }
        }
    }

    override fun getAdminCommand() {
        store.collection("admin")
                .whereEqualTo("command","stop")
                .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    return
                }
                if (querySnapshot != null) {
                    for (dc: DocumentChange in querySnapshot.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            view.showError()
                        }
                    }
                }
            }

        })
    }

}