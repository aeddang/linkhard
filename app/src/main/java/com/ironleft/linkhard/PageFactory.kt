package com.ironleft.linkhard
import android.content.pm.ActivityInfo
import com.ironleft.linkhard.page.*

import com.lib.page.PageFragment
import com.lib.page.PagePosition

class PageFactory {

    companion object {
        private var currentInstance: PageFactory? = null
        fun getInstance(): PageFactory {
            if (currentInstance == null) currentInstance = PageFactory()
            return currentInstance !!
        }

        fun getCategoryIdx(pageID: PageID):Int{
            return pageID.position.toString().first().toString().toInt()
        }
    }

    init {
        currentInstance = this
    }

    /**
     * 홈페이지 등록
     * 등록시 뒤로실행시 옙종료
     */
    val homePages: Array<PageID> = arrayOf()

    /**
     * 히스토리 사용안함
     * 등록시 뒤로실행시 패스
     */
    val disableHistoryPages: Array<PageID> = arrayOf(PageID.INTRO, PageID.SETUP_INIT)

    /**
     * 재사용가능 페이지등록
     * 등록시 viewModel 및 fragment가 재사용 -> 페이지 재구성시 효율적
     */
    val backStackPages: Array<PageID> = arrayOf()


    private val fullScreenPage: Array<PageID> = arrayOf()
    fun isFullScreenPage(id: PageID): Boolean {
        return fullScreenPage.indexOf(id) != - 1
    }


    fun getPageOrientation(id: PageID): Int {
        return when (id) {
            PageID.INTRO -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }



    fun getPageByID(id: PageID): PageFragment {
        return when (id) {
            PageID.INTRO -> PageIntro()
            PageID.SETUP_INIT -> PageSetupInit()
            PageID.DIR -> PageDir()
            PageID.SETUP_SERVER -> PageSetupServer()

        }
    }
}

/**
 * PageID
 * position 값에따라 시작 에니메이션 변경
 * 기존페이지보다 클때 : 오른쪽 -> 왼족
 * 기존페이지보다 작을때 : 왼쪽 -> 오른쪽
 * history back 반대
 */
enum class PageID(val resId: Int, override var position: Int = 9999) : PagePosition {
    //group1
    INTRO(0,0),
    SETUP_INIT(1000,1),
    DIR(100,100),
    SETUP_SERVER(1001,1000),

}