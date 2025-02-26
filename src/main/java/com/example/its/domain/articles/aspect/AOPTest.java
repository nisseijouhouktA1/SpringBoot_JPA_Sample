package com.example.its.domain.articles.aspect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 *@author kitahara
 *
 *ログ機能を実装する。
 *下のアノテーションで指定された範囲ので各種メソッドにログを取得しファイルに出力する。
 * ComponentでDIコンテナに登録されてアノテーションの自動呼出しで多分引っ張られてる。
 * デフォルトはLogback(実態)とslf4j(インターフェース)
 * 
 * インターセプターを作る場合は(HTTPメソッドのチェックとか)@Overrideを使って上書きする。
 * AOPに引き摺られないようにする。(使わないときは使わないため)
 * 
 * Httpフィルター(アクセスログを作るとかの場合)をミドルウェアに使いたい場合は、filterChain.doFilter(request, response)を使おう。
 **/
@Aspect
@Component
public class AOPTest {

	private static final Logger logger = LoggerFactory.getLogger(AOPTest.class);
	//フォーマット整える
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	  private ThreadLocal<Long> threadStartTime = new ThreadLocal<>();

	//引数だけのパターン、戻り値だけのパターンで2つメソッドを実装する。
	//これが終わってから。(いったんコミットを入れること)
	//@afterReturningを調べる。
	//あんまり綺麗じゃない書き方かも
	/*
	 *@Beforeアノテーションは非常に便利ですが、いくつか注意すべきポイントがあります。
	 *例えば、@Beforeで例外をスローすると、対象のメソッドが実行されなくなります。
	 *また、処理が重い場合はパフォーマンスに影響を与える可能性があります。
	 * ログ記録や簡単なチェック処理など、比較的軽い処理に留めるほうが良いかも。
	 * 非同期処理とかは除外しよう。
	 * ProceedingJoinPointは@around限定だ。
	 * 
	 * */
	@Before("execution(* com.example.*.*.*.*(..))")
	public void before(JoinPoint joinpoint) {
		long start = System.currentTimeMillis();
		threadStartTime.set(start);
		/*
		 * Arrays.toString()
		 * 
		 * 
		 * */		
		LocalDateTime startTime = LocalDateTime.now();
		String formattedStartTime = startTime.format(formatter);
		logger.info("------------------------------------メソッドの実行前-------------------------------");
		logger.info("method start : " + joinpoint.getSignature() + " at :" + formattedStartTime);
		//mustacheフォーマットで書くと読みやすい。
		 logger.info("arguments as : {}", Arrays.toString(joinpoint.getArgs()));


	}
	
	//正常にメソッドが終了した場合
	//正常にメソッドが終了しなかったときは別に対応する。
	@AfterReturning(pointcut = "execution(* com.example.*.*.*.*(..))", returning = "result")
	public void normalReturn(JoinPoint joinpoint,Object result) {
		long start = threadStartTime.get();
		long end = System.currentTimeMillis();
		logger.info("------------------------------------メソッドの実行後-------------------------------");
		logger.info("メソッドの実行時間は：" + (end - start) + "ms");
		logger.info("return value as :" + result);
		
	}
}
