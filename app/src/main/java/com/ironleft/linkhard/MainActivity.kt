package com.ironleft.linkhard

import android.content.Intent
import android.view.View
import com.ironleft.linkhard.store.FileUploadManager
import com.lib.page.PageActivity
import com.lib.page.PageFragment
import com.lib.page.PagePresenter
import com.lib.util.CommonUtil
import com.skeleton.rx.Rx
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : PageActivity<PageID>(), Rx {

    private val appTag = javaClass.simpleName
    override fun getLayoutResId(): Int = R.layout.activity_main
    override fun getPageExitMsg(): Int = R.string.notice_app_exit
    override fun getPageAreaId(): Int = R.id.area

    @Inject lateinit var fileUploadManager: FileUploadManager

    override fun onCreatedView() {
        AndroidInjection.inject(this)
        PagePresenter.getInstance<PageID>().pageStart(PageID.INTRO)

    }

    override fun onDestroyedView() {
        fileUploadManager.onDestroyed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            fileUploadManager.onActivityResult(requestCode, resultCode, it)
        }

    }


    override fun onWillChangePageFragment(id: PageID, param: Map<String, Any?>?, isPopup: Boolean) {
        loaded()
        val isFullScreen = PageFactory.getInstance().isFullScreenPage(id)
        if (isFullScreen) CommonUtil.enterFullScreenMode(this)
        else CommonUtil.enterDefaultMode(this)
    }

    override fun onResume() {
        super.onResume()
        currentTopPage?.let {
            val isFullScreen = PageFactory.getInstance().isFullScreenPage(it)
            if (isFullScreen) CommonUtil.enterFullScreenMode(this)
            else CommonUtil.enterDefaultMode(this)
        }

    }

    override fun getPageByID(id: PageID): PageFragment {
        return PageFactory.getInstance().getPageByID(id)
    }

    override fun getPopupByID(id: PageID): PageFragment {
        return PageFactory.getInstance().getPageByID(id)
    }

    override fun getHomes(): Array<PageID> {
        return PageFactory.getInstance().homePages
    }

    override fun getDisableHistorys(): Array<PageID>? {
        return PageFactory.getInstance().disableHistoryPages
    }

    override fun getBackStacks(): Array<PageID> {
        return PageFactory.getInstance().backStackPages
    }

    override fun loading(isRock: Boolean) {
        dimed.visibility = View.VISIBLE
        loadingBar.visibility = View.VISIBLE
    }

    override fun loaded() {
        dimed.visibility = View.GONE
        loadingBar.visibility = View.GONE
    }



    override fun getPageIn(isBack: Boolean): Int = if (isBack) R.anim.slide_in_left else R.anim.slide_in_right
    override fun getPageOut(isBack: Boolean): Int = if (isBack) R.anim.slide_out_right else R.anim.slide_out_left
    override fun getPopupIn(): Int = R.anim.slide_in_down
    override fun getPopupOut(): Int = R.anim.slide_out_down

}
