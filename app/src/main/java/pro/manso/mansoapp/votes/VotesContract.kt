package pro.manso.mansoapp.votes

import pro.manso.mansoapp.utils.BaseContract

interface VotesContract : BaseContract {

    interface View: BaseContract.View{
        fun displayVotes(votes: String)
        fun showError()
    }

    interface Presenter: BaseContract.Presenter {
        fun getAdminCommand()
        fun observeRates()
    }
}