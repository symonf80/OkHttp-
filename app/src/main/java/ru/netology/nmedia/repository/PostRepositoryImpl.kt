package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}
    private val typeTokenPost = object : TypeToken<Post>() {}

    companion object {
         const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }


    override fun getAllAsync(callback: PostRepository.GetResultCallback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .build()
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()

                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(responseBody))
                    }
                    try {
                        callback.onSuccess(gson.fromJson(responseBody, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }


    override fun likeByIdAsync(post: Post, callback: PostRepository.GetResultCallback<Post>) {
        val id = post.id
        val request: Request = if (!post.likedByMe) {
            Request.Builder()
                .post(gson.toJson(post).toRequestBody(jsonType))
                .url("${BASE_URL}/api/posts/$id/likes")
                .build()
        } else {
            Request.Builder()
                .delete()
                .url("${BASE_URL}/api/posts/$id/likes")
                .build()
        }
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()

                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(responseBody))
                    }
                    try {
                        callback.onSuccess(gson.fromJson(responseBody, typeTokenPost.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }


    override fun saveAsync(post: Post, callback: PostRepository.GetResultCallback<Post>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(body))
                        return
                    }
                    try {
                        callback.onSuccess(gson.fromJson(body, typeTokenPost.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.GetResultCallback<Unit?>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()

                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(responseBody))
                        return
                    }
                    try {
                        callback.onSuccess(gson.fromJson(responseBody, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }
}
