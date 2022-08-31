# ShowYUVwithJOGL

JavaとOpenGL(JOGL)を使用した、YUV⇒RGB変換、動画表示のサンプルコードです。  
最初はSwingのJPanelに描画しています。  
YUV⇒RGB変換については最初はJavaで記述しますが、最終的にはGLSLを用いてGPU側で行うようにします。  
IDEはVisual Studio Codeを使用しています。  
プロジェクト管理ツールとしてMavenを使用しています。

なおYUVのファイルは[Arizona State Universityのページ](http://trace.eas.asu.edu/yuv/)のCIFファイルを使用してください。
