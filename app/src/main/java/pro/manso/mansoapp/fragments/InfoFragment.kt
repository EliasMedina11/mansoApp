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
import pro.manso.mansoapp.getBiggerImage
import pro.manso.mansoapp.models.TotalMessagesEvent
import pro.manso.mansoapp.utils.CircleTransform
import pro.manso.mansoapp.utils.RxBus
import java.util.EventListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage


class InfoFragment : Fragment() {

    private lateinit var _view: View

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference

    private var chatSubscription: ListenerRegistration? = null

    private var voteCount = 0
    private var voteCount2 = 0

    private lateinit var mStorageRef: StorageReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_info, container, false)

        mStorageRef = FirebaseStorage.getInstance().reference
        setUpChatDB()
        setUpCurrentUser()
        setUpCurrentUserInformation()

        /*  if (currentUser.displayName == null || currentUser.photoUrl == null) {
            alertDialogChangeProfilePicAndName()
        }
        */

        //Total messages Firebase style
        //subscribeToTotalMessagesFirebaseStyle()
        subscribeToTotalMessagesEventBusReactiveStyle()
      //  observeRates()
        return _view
    }

     fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpChatDB() {
        chatDBRef = store.collection("chat")
    }

    private fun setUpCurrentUserInformation() {
        _view.textViewInfoEmail.text = currentUser.email
        _view.textViewInfoName.text = currentUser.displayName?.let { it } ?: run { currentUser.email.toString() }

        currentUser.photoUrl?.let {
            Picasso.with(context).load(getBiggerImage(currentUser)).resize(300, 300)
                    .centerCrop().transform(CircleTransform()).into(_view.imageViewInfoAvatar)
        } ?: run {
            Picasso.with(context).load(R.drawable.background_manso_main).resize(300, 300)
                    .centerCrop().transform(CircleTransform()).into(_view.imageViewInfoAvatar)
        }

    }

    private fun alertDialogChangeProfilePicAndName() {
        _view.imageViewInfoAvatar.setOnClickListener {

            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("Profile Image")
            builder.setMessage("¿Quieres cambiar tu imagen de perfil?")
            builder.setPositiveButton("SI") { _, _ ->
            }

            builder.setNegativeButton("NO") { dialog, _ ->
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

    private fun subscribeToTotalMessagesEventBusReactiveStyle(){
        RxBus.listen(TotalMessagesEvent::class.java).subscribe {
            _view.textViewInfoTotalMessages.text = "${it.total}"
        }
    }

    override fun onDestroyView() {
        chatSubscription?.remove()
        super.onDestroyView()
    }

}
