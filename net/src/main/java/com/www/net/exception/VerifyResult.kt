package com.www.net.exception

class VerifyResult {
    companion object {
        fun verify(result: String): Boolean {
            if (result.isEmpty()) {
                return false
            }
            return true
        }
    }

}