package ru.netology.nmedia.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetResultCallback<List<Post>> {
            override fun onSuccess(result: List<Post>) {
                _data.postValue(FeedModel(posts = result, empty = result.isEmpty()))
            }

            override fun onError(e: Throwable) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let { post ->
            repository.saveAsync(post, object : PostRepository.GetResultCallback<Post> {
                override fun onError(e: Throwable) {
                    _postCreated.postValue(Unit)
                    _data.postValue(FeedModel(error = true))
                }

                override fun onSuccess(result: Post) {
                    _data.postValue(
                        FeedModel(posts = _data.value?.posts.orEmpty()
                            .map { if (it.id == result.id) result else it })
                    )

                }
            })

        }

        edited.value = empty

    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (text == edited.value?.content) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(post: Post) {
        repository.likeByIdAsync(post, object : PostRepository.GetResultCallback<Post> {
            override fun onError(e: Throwable) {
                Toast.makeText(getApplication(), R.string.error_server, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(result: Post) {
                _data.postValue(
                    FeedModel(posts = _data.value?.posts
                        .orEmpty().map {
                            if (it.id == result.id) result else it
                        })
                )
            }
        })
    }

    fun removeById(id: Long) {
        repository.removeByIdAsync(id, object : PostRepository.GetResultCallback<Unit?> {
            override fun onError(e: Throwable) {
                _data.postValue(_data.value?.copy(posts = _data.value?.posts.orEmpty()))
                Toast.makeText(getApplication(), R.string.error_server, Toast.LENGTH_SHORT).show()

            }

            override fun onSuccess(result: Unit?) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id })

                )

            }
        })


    }
}
