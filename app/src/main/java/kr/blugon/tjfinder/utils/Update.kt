package kr.blugon.tjfinder.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import fuel.httpGet
import kr.blugon.tjfinder.VERSION
import org.json.JSONArray

data class Version(
    val version: String,
    val downloadUrl: String,
    val description: String
)

suspend fun versions(): List<Version>? {
    val url = "https://api.github.com/repos/blugon0921/TjFinder/releases"
    val response = url.httpGet()
    if(response.statusCode != 200) return null
    val body = response.body
    val versionArray = JSONArray(body)
    return ArrayList<Version>().apply {
        for(i in 0 until versionArray.length()) {
            val version = versionArray.getJSONObject(i)
            val name = version.getString("name")
            val assets = version.getJSONArray("assets")
            val asset = assets.getJSONObject(0)
            val downloadUrl = asset.getString("browser_download_url")
            val description = version.getString("body")
            add(Version(name, downloadUrl, description))
        }
        reverse()
//        sortBy {
//            val version = it.version.split(".")
//            version[0].toInt()*10000 + version[1].toInt()*100 + version[2].toInt()
//        }
    }
}

suspend fun latestVersion(): Version? {
    val versions = versions() ?: return null
    return versions.lastOrNull()
}

fun isLatestVersion(latest: Version): Boolean {
    return latest.version == VERSION
}

fun downloadApk(context: Context, version: Version) {
    val fileName = "TjFinder-${version.version}.apk"
    val request = DownloadManager.Request(Uri.parse(version.downloadUrl)).apply {
        setTitle(fileName)
        setDescription("다운로드중...")
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
    }

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)

    Toast.makeText(context, "다운로드를 시작합니다", Toast.LENGTH_SHORT).show()
}