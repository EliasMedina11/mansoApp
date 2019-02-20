package pro.manso.mansoapp.utils

interface BaseContract {
    interface Presenter

    interface View {
        fun showLoading(showLoading: Boolean)
    }
}