import http from '../http'

export default {
    login(data, success) {
        console.log(data);
        http.postBody('user/login', data, success);
    }
}