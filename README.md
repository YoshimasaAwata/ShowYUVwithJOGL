# ShowYUVwithJOGL

JavaとOpenGL(JOGL)を使用した、YUV⇒RGB変換、動画表示のサンプルコードです。  
最初はSwingのJPanelに描画しています。  
YUV⇒RGB変換については最初はJavaで記述しますが、最終的にはGLSLを用いてGPU側で行うようにします。  
IDEはVisual Studio Codeを使用しています。  
プロジェクト管理ツールとしてMavenを使用しています。

なおYUVのファイルは[Arizona State Universityのページ](http://trace.eas.asu.edu/yuv/)のCIFファイルを使用してください。

詳細な説明は
[YUV⇒RGB変換(JOGL編)](https://yoshia.mydns.jp/programming/?p=502)
を参照してください。

tagは以下の章に対応しています。

- base-window: 2.3 main関数でメインウィンドウの作成、表示設定
- shou-yuv-with-jpanel: 3.4 Timerで動画表示
- draw-square: 4.8 JOGLによる四角の描画の確認
- show-yuv-with-rgb-texture: 4.11 タイマーをアニメーターに変更
