package pro.manso.mansoapp.votes

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_votes.*
import kotlinx.android.synthetic.main.fragment_votes.*
import pro.manso.mansoapp.MainActivity
import pro.manso.mansoapp.R
import pro.manso.mansoapp.goToActivity

class VotesActivity : AppCompatActivity(), VotesContract.View {

    private lateinit var presenter : VotesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_votes)
        presenter = VotesPresenter(this)
        presenter.observeRates()
        presenter.getAdminCommand()
    }
    override fun displayVotes(votes: String) {
        val totalVotes = presenter.counter1
        textViewVote1.text = totalVotes.toString()
        manso_voto.layoutParams.height =  totalVotes + 10
    }

    override fun onBackPressed() {}
    /*
        if (vote == "1"){
            do{
                viewCounter += 10
            }
                while ( viewCounter >= 500)
            imageViewVote1.layoutParams.height = viewCounter
            counter++
            toast("voto1 " + counter.toString())
            textViewVote1.text.toString().trim()
            textViewVote1.text = counter.absoluteValue.toString()
        } else {
            do{ viewCounter2 += 10 }
            while ( viewCounter2 >= 500)
            imageViewVote2.layoutParams.height = viewCounter2
            toast("voto2 "+ counter2.toString())
            textViewVote2.text = counter2++.toString()
        }
       showLoading(false)
       */

    override fun showLoading(showLoading: Boolean) {
        if (!showLoading) {
            view_loading.visibility = View.GONE
        } else {
            view_loading.visibility = View.VISIBLE
        }
    }

    override fun showError() {
        goToActivity<MainActivity> {  }
    }

}




