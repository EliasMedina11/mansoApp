package pro.manso.mansoapp.fragments


import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import pro.manso.mansoapp.toast
import pro.manso.mansoapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_info.view.*
import pro.manso.mansoapp.utils.CircleTransform
import java.util.EventListener


class InfoFragment : Fragment() {

    private lateinit var _view: View

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference

    private var chatSubscription: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_info, container, false)

        setUpChatDB()
        setUpCurrentUser()
        setUpCurrentUserInformation()

        /*  if (currentUser.displayName == null || currentUser.photoUrl == null) {
            alertDialogChangeProfilePicAndName()
        }
        */

        //Total messages Firebase style
        subscribeToTotalMessagesFirebaseStyle()

        return _view
    }

    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpChatDB() {
        chatDBRef = store.collection("chat")

    }

    private fun setUpCurrentUserInformation() {
        _view.textViewInfoEmail.text = currentUser.email
        _view.textViewInfoName.text = currentUser.displayName?.let { it } ?: run { currentUser.email.toString() }

        currentUser.photoUrl?.let {
            Picasso.get().load(currentUser.photoUrl).resize(300, 300)
                    .centerCrop().transform(CircleTransform()).into(_view.imageViewInfoAvatar)
        } ?: run {
            Picasso.get().load(R.drawable.background_manso_main).resize(300, 300)
                    .centerCrop().transform(CircleTransform()).into(_view.imageViewInfoAvatar)
        }

    }

    private fun alertDialogChangeProfilePicAndName() {
        _view.imageViewInfoAvatar.setOnClickListener {

            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("Profile Image")
            builder.setMessage("Do you want to change your name?")
            builder.setPositiveButton("YES") { dialog, which ->

            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()

            dialog.show()
        }

    }

    private fun subscribeToTotalMessagesFirebaseStyle() {
        chatDBRef.addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    activity!!.toast("Exception!")
                    return
                }
                querySnapshot?.let { _view.textViewInfoTotalMessages.text = "${it.size()}" }
            }

        })
    }

    override fun onDestroyView() {
        chatSubscription?.remove()
        super.onDestroyView()
    }

}
