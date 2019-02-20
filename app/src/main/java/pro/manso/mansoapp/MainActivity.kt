package pro.manso.mansoapp

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import pro.manso.mansoapp.adapters.PagerAdapter
import pro.manso.mansoapp.fragments.ChatFragment
import pro.manso.mansoapp.fragments.InfoFragment
import pro.manso.mansoapp.fragments.PollFragment
import com.eliasmedina.mylibrary.ToolbarActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_main.*
import pro.manso.mansoapp.activities.LoginActivity
import pro.manso.mansoapp.models.CommandEvent
import pro.manso.mansoapp.utils.RxBus


open class MainActivity : ToolbarActivity() {


    private  var prevBottomSelected: MenuItem? = null
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private var cacheExpiration: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbarToLoad(toolbarView as Toolbar)
        setupViewPager(getPagerAdapter())
        setUpBottomNavigationBar()

        remoteConfig = FirebaseRemoteConfig.getInstance()

        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        remoteConfig.setConfigSettings(configSettings)

        remoteConfig.setDefaults(R.xml.remote_config_defaults)

        val isUsingDeveloperMode = remoteConfig.info.configSettings.isDeveloperModeEnabled

         cacheExpiration = if(isUsingDeveloperMode) {
            0
        } else {
            3600
        }

    }

    fun testFetch(){
        remoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener() {task ->
                    if (task.isSuccessful) {
                        remoteConfig.activateFetched()
                    } else {
                        Log.e("error", "Fetch Failed")
                    }
                    RxBus.publish(CommandEvent(remoteConfig.getBoolean(VOTE_ENABLED),remoteConfig.getBoolean(VOTE_ENDED)))
                }
    }

    private fun getPagerAdapter(): PagerAdapter {
        val adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(InfoFragment())
        adapter.addFragment(PollFragment())
        adapter.addFragment(ChatFragment())
        return adapter
    }

    private fun setupViewPager(adapter: PagerAdapter) {
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = adapter.count
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
               testFetch()
            }
            override fun onPageSelected(position: Int) {
                if (prevBottomSelected == null) {
                    bottomNavigation.menu.getItem(0).isChecked = false
                } else{
                    prevBottomSelected!!.isChecked = false
                }
                bottomNavigation.menu.getItem(position).isChecked = true
                prevBottomSelected = bottomNavigation.menu.getItem(position)
            }
        })
    }

    private fun setUpBottomNavigationBar (){
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_info -> {
                    viewPager.currentItem = 0; true
                }
                R.id.bottom_nav_poll -> {
                    viewPager.currentItem = 1; true
                }
                R.id.bottom_nav_chat -> {
                    viewPager.currentItem = 2; true
                }
                else -> { false }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.general_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.log_out -> {
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                goToActivity<LoginActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private const val TAG = "MainActivity"

        // Remote Config keys
        const val VOTE_ENABLED = "vote_enabled"
        private const val VOTE_ENDED = "vote_clean"
        private const val WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps"
    }
}

