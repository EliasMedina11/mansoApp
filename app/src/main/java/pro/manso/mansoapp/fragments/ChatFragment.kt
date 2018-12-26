package pro.manso.mansoapp.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import pro.manso.mansoapp.R
import pro.manso.mansoapp.adapters.ChatAdapter
import pro.manso.mansoapp.models.Message
import pro.manso.mansoapp.toast
import pro.manso.mansoapp.utils.filters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import pro.manso.mansoapp.models.TotalMessagesEvent
import pro.manso.mansoapp.utils.RxBus
import java.util.*
import java.util.EventListener

class ChatFragment : Fragment() {

    private lateinit var _view: View
    private lateinit var adapter: ChatAdapter
    private val messageList: ArrayList<Message> = ArrayList()

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference

    private var chatSubscription: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_chat, container, false)

        setUpChatDB()
        setUpCurrentUser()
        setUpRecyclerView()
        setUpChatButton()

        subscribeToChatMessages()

        return _view
    }

    private fun setUpChatButton() {
        _view.buttonSend.setOnClickListener {
            val messageText = editTextMessage.text.toString()
            val currentUserName = currentUser.displayName?.let { it } ?: run { currentUser.email.toString() }
            if (messageText.length >= 30 || filters.contains(messageText)){
                activity!!.toast("Porfavor no spam ni palabras prohibidas intenta de nuevo")
                _view.editTextMessage.setText("")
            } else {
                if (messageText.isNotEmpty()) {
                    val photo = currentUser.photoUrl?.let { currentUser.photoUrl.toString() }
                            ?: run { "" }
                    val message = Message(currentUser.uid, messageText, photo, Date(), currentUserName!!, currentUser.email!!)
                    saveMessage(message)
                    _view.editTextMessage.setText("")
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        adapter = ChatAdapter(messageList, currentUser.uid)

        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()
        _view.recyclerView.adapter = adapter
    }

    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpChatDB() {
        chatDBRef = store.collection("chat")

    }

    private fun saveMessage(message: Message) {
        val newMessage = HashMap<String, Any>()
        newMessage["authorId"] = message.authorId
        newMessage["message"] = message.message
        newMessage["profileImageURL"] = message.profileImageURL
        newMessage["sentAt"] = message.sentAt
        newMessage["displayName"] = message.displayName
        newMessage["userEmail"] = message.userEmail

        chatDBRef.add(newMessage)
                .addOnFailureListener {
                    activity!!.toast("Message error, try again!")
                }
    }

    private fun subscribeToChatMessages() {
        chatSubscription = chatDBRef
                .orderBy("sentAt", Query.Direction.DESCENDING)
                .limit(100)
                .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {

                    override fun onEvent(snapShot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                        exception?.let {
                            activity!!.toast("Exception!")
                            return
                        }

                        snapShot?.let {
                            messageList.clear()
                            val messages = it.toObjects(Message::class.java)
                            messageList.addAll(messages.asReversed())
                            adapter.notifyDataSetChanged()
                            _view.recyclerView.smoothScrollToPosition(messageList.size)
                            RxBus.publish(TotalMessagesEvent(messageList.size))
                        }
                    }
                })
    }

    override fun onDestroyView() {
        chatSubscription?.remove()
        super.onDestroyView()
    }

}
