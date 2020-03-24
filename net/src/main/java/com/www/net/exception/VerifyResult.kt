package com.www.net.exception

class VerifyResult {
    companion object {
        fun verify(result: String): Boolean {
            if (result.indexOf("{") > -1) {
                return true
            }
            return false
        }
    }

}