package com.yiqisport.yiqiapp.data

import java.io.File

/**
 * DataHelper to store, update, remove the complicated constructed or wide influence data
 */
class DataHelper {

    //region delete
    fun deleteFolderVideo(video : Video, databaseHelper: DatabaseHelper){
        //delete files
        deleteFilePath(video.localUrl)
        deleteFilePath(video.coverPath)

        databaseHelper.deleteFolderVideo(video)
        deleteHomeEvent(video, databaseHelper)
    }

    fun deleteDairy(diary : Diary, databaseHelper: DatabaseHelper){
        databaseHelper.deleteDairy(diary)
        deleteHomeEvent(diary, databaseHelper)
    }

    fun deleteFolder(folder : Folder, databaseHelper : DatabaseHelper){
        //delete files
        deleteFilePath(folder.profileImagePath)
        deleteFilePath(folder.backgroundImagePath)

        //delete all videos
        val videos = databaseHelper.getAllFolderVideoByFolderId(folder.id)
        for(video in videos){
            deleteFolderVideo(video, databaseHelper)
        }

        //delete all videos
        val diaries = databaseHelper.getAllDairyByFolderId(folder.id)
        for(diary in diaries){
            deleteDairy(diary, databaseHelper)
        }

        databaseHelper.deleteFolder(folder)
    }

    //endregion

    //region update
    fun updateVideo(video : Video, databaseHelper: DatabaseHelper){
        databaseHelper.updateVideo(video)
        updateHomeEvent(video, databaseHelper)
    }

    fun updateFolder(folder: Folder, databaseHelper: DatabaseHelper){
        if(databaseHelper.findFolderById(folder.id)!!.name != folder.name){
            for(sv in databaseHelper.getAllFolderVideoByFolderId(folderId = folder.id)){
                sv.folderName = folder.name
                updateVideo(sv, databaseHelper)
            }
            for(diary in databaseHelper.getAllDairyByFolderId(folderId = folder.id)){
                diary.folderName = folder.name
                updateDairy(diary, databaseHelper)
            }
        }
        databaseHelper.updateFolder(folder)
    }

    fun updateDairy(diary: Diary, databaseHelper: DatabaseHelper){
        databaseHelper.updateDairy(diary)
        updateHomeEvent(diary, databaseHelper)
    }

    //endregion

    //region add
    fun addFolder(folder : Folder, databaseHelper: DatabaseHelper) : Long{
        val id = databaseHelper.addFolder(folder)
        folder.id = id.toInt()
        return id
    }

    fun addVideo(video : Video, databaseHelper: DatabaseHelper) : Long{
        val id = databaseHelper.addFolderVideo(video)
        video.id = id.toInt()
        addHomeEvent(video, databaseHelper)
        return id
    }

    fun addDairy(diary : Diary, databaseHelper: DatabaseHelper) : Long{
        val id = databaseHelper.addDairy(diary)
        diary.id = id.toInt()
        addHomeEvent(diary, databaseHelper)
        return id
    }

    //endregion

    //region home event

    private fun deleteHomeEvent(homeEventObject: HomeEventObject, databaseHelper: DatabaseHelper){
        val homeEvent = databaseHelper.findHomeEventByRef(homeEventObject)
        if(homeEvent != null) databaseHelper.deleteHomeEvent(homeEvent)
    }

    private fun updateHomeEvent(homeEventObject: HomeEventObject, databaseHelper: DatabaseHelper){
        val homeEvent = databaseHelper.findHomeEventByRef(homeEventObject)
        homeEvent!!.update(homeEventObject)
        databaseHelper.updateHomeEvent(homeEvent)
    }

    private fun addHomeEvent(homeEventObject: HomeEventObject, databaseHelper: DatabaseHelper){
        databaseHelper.addHomeEvent(HomeEvent(homeEventObject))
    }

    //endregion

    //region helper
    private fun deleteFilePath(path : String){
        File(path).delete()
    }

    //endregion
}