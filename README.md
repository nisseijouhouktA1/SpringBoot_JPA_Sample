現在更新中 2025 2/06

Spring Securityのディレクトリ別認証とCSRFトークンでのチェックの追加の為、Postはできなくなってます。。
csrfをなくしたい場合はFormConfigファイルとdependencyのSpringSecurity部分を削除するか全面コメントアウトしてください…

初めに、
アプリケーションの起動方法について：

Spring initializrで検索を行うと初期テンプレートをディレクトリからサくせしてくれるサイトがあります。
こちらから"GENERATE"で作成してください。

開発環境は こちらはJava(21),eclipse最新版(現在の2024のバージョンで大丈夫です。)
eclipseのmarketplaceでSTSプラグイン(4)をインストールしてください。

それで動作確認が可能になるかと思います。下記の様にビルドツールはgradle(groovy)でお願いいたします。
dependencyはinitializrで追加をしてもこちらのコードで上書きしても問題ないです。
また、MySQL環境で構築したためMYySQLサーバーが必要になります。
マイグレーション等のツールは付属していないため、手動でMySQLサーバーに接続してパス、DB名、テーブルのDDL文を実行してください。
(application.propertyにパス等の設定を記載する箇所があります。自身の環境に合わせて変更が可能です。デバッグのオンオフなどもここに記載します。)
デバッグをしながら行う場合、eclipseはコマンドプロンプトにログが流れるアイコンじゃない方のexeの実行を行ったほうが良いかもです。。

![image](https://github.com/user-attachments/assets/34833853-f63f-43bf-9bca-5f0972d8d6a7)
