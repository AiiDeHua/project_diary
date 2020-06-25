package com.xuanyuetech.tocoach.data

import java.io.File

/**
 * DataHelper to store, update, remove the complicated constructed or wide influence data
 */
class DataHelper {

    //region delete
    fun deleteStudentVideo(video : Video, databaseHelper: DatabaseHelper){
        //delete files
        deleteFilePath(video.localUrl)
        deleteFilePath(video.coverPath)

        databaseHelper.deleteStudentVideo(video)
        deleteHomeEvent(video, databaseHelper)
    }

    fun deleteDairy(diary : Diary, databaseHelper: DatabaseHelper){
        databaseHelper.deleteDairy(diary)
        deleteHomeEvent(diary, databaseHelper)
    }

    fun deleteStudent(student : Student, databaseHelper : DatabaseHelper){
        //delete files
        deleteFilePath(student.profileImagePath)
        deleteFilePath(student.backgroundImagePath)

        //delete all videos
        val videos = databaseHelper.getAllStudentVideoByStudentId(student.id)
        for(video in videos){
            deleteStudentVideo(video, databaseHelper)
        }

        //delete all videos
        val diaries = databaseHelper.getAllDairyByStudentId(student.id)
        for(diary in diaries){
            deleteDairy(diary, databaseHelper)
        }

        databaseHelper.deleteStudent(student)
    }

    //endregion

    //region update
    fun updateVideo(video : Video, databaseHelper: DatabaseHelper){
        databaseHelper.updateVideo(video)
        updateHomeEvent(video, databaseHelper)
    }

    fun updateStudent(student: Student, databaseHelper: DatabaseHelper){
        if(databaseHelper.findStudentById(student.id)!!.name != student.name){
            for(sv in databaseHelper.getAllStudentVideoByStudentId(studentId = student.id)){
                sv.studentName = student.name
                updateVideo(sv, databaseHelper)
            }
            for(diary in databaseHelper.getAllDairyByStudentId(studentId = student.id)){
                diary.studentName = student.name
                updateDairy(diary, databaseHelper)
            }
        }
        databaseHelper.updateStudent(student)
    }

    fun updateDairy(diary: Diary, databaseHelper: DatabaseHelper){
        databaseHelper.updateDairy(diary)
        updateHomeEvent(diary, databaseHelper)
    }

    //endregion

    //region add
    fun addStudent(student : Student, databaseHelper: DatabaseHelper) : Long{
        val id = databaseHelper.addStudent(student)
        student.id = id.toInt()
        return id
    }

    fun addVideo(video : Video, databaseHelper: DatabaseHelper) : Long{
        val id = databaseHelper.addStudentVideo(video)
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