package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun saveAsync(post: Post, callback: GetResultCallback<Post>)
    fun removeByIdAsync(id: Long, callback: GetResultCallback<Unit?>)
    fun getAllAsync(callback: GetResultCallback<List<Post>>)
    fun likeByIdAsync(post: Post, callback: GetResultCallback<Post>)


    interface GetResultCallback<T> {
        fun onSuccess(result: T) {}
        fun onError(e: Exception) {}
    }


}
