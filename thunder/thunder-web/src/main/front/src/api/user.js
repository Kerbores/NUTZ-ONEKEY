import http from '../http'

export default {
    login(loginForm, success) {
        http.postBody('user/login', loginForm, success);
    },
    list(page, success) {
        http.get('user/list', {
            page: page
        }, success);
    },
    search(page, key, success) {
        http.get('user/search', {
            page: page,
            key: key
        }, success);
    },
    save(user, success) {
        http.postBody('user/add', user, success)
    },
    update(user, success) {
        http.postBody('user/edit', user, success)
    },
    delete(id, success) {
        http.get('user/delete/' + id, {}, success);
    }
}