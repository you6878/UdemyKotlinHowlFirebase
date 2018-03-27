package com.bpplatform.howlfirebaseauth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var authStateListener : FirebaseAuth.AuthStateListener? = null
    var callbackManager : CallbackManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //로그인 세션을 체크하는 부분
        callbackManager = CallbackManager.Factory.create()
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            //세션
            var user = firebaseAuth.currentUser
            if(user != null){
                startActivity(Intent(this,HomeActivity::class.java))
            }

        }
        //구글 로그인 옵션
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        //구글 로그인 클래스를 만듬
        var googleSignInClient = GoogleSignIn.getClient(this,gso)

        button_signup.setOnClickListener {
            createEmailId()
        }
        button_login.setOnClickListener {
            loginId()
        }
        button_findPassword.setOnClickListener {
            startActivity(Intent(this,FindPasswordActivity::class.java))
        }
        button_google.setOnClickListener {
            var signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent,1)
        }
        button_facebook.setReadPermissions("email","public_profile")
        button_facebook.registerCallback(callbackManager,object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                var credential = FacebookAuthProvider.getCredential(result!!.accessToken.token)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            task ->
                            if(task.isSuccessful){
                                Toast.makeText(this@MainActivity,"페이스북 연동이 성공 하였습니다.",Toast.LENGTH_LONG).show()
                            }else{
                                Toast.makeText(this@MainActivity,task.exception.toString(),Toast.LENGTH_LONG).show()
                            }
                        }
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {

            }

        })
    }
    fun createEmailId(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(editText_email.text.toString(),editText_password.text.toString())
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"회원가입에 성공하였습니다.",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()
                    }

                }

    }
    fun loginId(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(editText_email.text.toString(),editText_password.text.toString())
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){

                        Toast.makeText(this,"로그인에 성공하였습니다.",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener!!)
    }

    override fun onPause() {
        super.onPause()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode,resultCode,data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            //구글 로그인에 성공했을때 넘어 오는 토큰 값을 가지고 있는 Task
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            //ApiException 캐스팅
            var account = task.getResult(ApiException::class.java)
            //Credentail 구글 로그인에 성공했다는 인증서
            var credential = GoogleAuthProvider.getCredential(account.idToken,null)
            //파이어베이스에 구글 사용자 등록
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener {
                        task ->
                        if(task.isSuccessful){
                            Toast.makeText(this,"구글 아이디 연동이 성공하였습니다.",Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()
                        }
                    }
        }
    }
}
