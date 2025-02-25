package com.example.its.domain.articles.aspect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

	//引数だけのパターン、戻り値だけのパターンで2つメソッドを実装する。
	//これが終わってから。(いったんコミットを入れること)
	//@afterReturningを調べる。
	//多分これ全部
	//あんまり綺麗じゃない書き方かも
	@Around("execution(* com.example.*.*.*.*(..))")
	public Object around(ProceedingJoinPoint joinpoint) {
		long start = System.currentTimeMillis();

		LocalDateTime startTime = LocalDateTime.now();
		//フォーマット整える
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedStartTime = startTime.format(formatter);
		logger.info("method start :" + joinpoint.getSignature() + "at :" + formattedStartTime);
		Object result = null;
		try {
			//結果を取得しないと呼び出し元に結果が返っていかない
			//このAOPとかいうやつはそれぞれのメソッド処理にサイレントで組み込まれていることに注意。
			result = joinpoint.proceed();
		} catch (Throwable e) {
			e.printStackTrace();

			//細かい例外に合わせてログを振り分けると良いかもしれないです。
			//今回は省く。
			logger.error("Error番号XXが発生しました。", e);
		}
		long end = System.currentTimeMillis();
		logger.info("メソッドの実行時間は：" + (end - start) + "ms");
		LocalDateTime endTime = startTime.plusSeconds((end - start) / 1000);
		String formattedEndTime = endTime.format(formatter);
		logger.info("method end :" + "at :" + formattedEndTime);

		return result;
	}
}
