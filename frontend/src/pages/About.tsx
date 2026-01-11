function About() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="container mx-auto px-4 py-12">
        <div className="max-w-3xl mx-auto bg-white rounded-lg shadow-xl p-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-6">GachaHubについて</h1>

          <div className="space-y-6 text-gray-700">
            <p className="text-lg leading-relaxed">
              このアプリは、バンダイやタカラトミーなどのメーカー公式サイトから
              ガチャガチャの最新情報を自動収集し、Webアプリとして表示するシステムです。
            </p>

            <div className="border-l-4 border-indigo-500 pl-4 py-2">
              <h2 className="text-2xl font-semibold text-gray-800 mb-3">主な機能</h2>
              <ul className="list-disc list-inside space-y-2">
                <li>メーカー公式サイトからの自動スクレイピング</li>
                <li>新商品の通知機能</li>
                <li>メーカー別・価格別フィルタリング</li>
                <li>お気に入り登録機能</li>
              </ul>
            </div>

            <div className="bg-indigo-50 rounded-lg p-6">
              <h2 className="text-2xl font-semibold text-gray-800 mb-3">対応メーカー</h2>
              <div className="flex flex-wrap gap-3">
                <span className="bg-white px-4 py-2 rounded-full text-indigo-700 font-medium shadow">
                  バンダイ
                </span>
                <span className="bg-white px-4 py-2 rounded-full text-indigo-700 font-medium shadow">
                  タカラトミー
                </span>
                <span className="bg-white px-4 py-2 rounded-full text-gray-500 font-medium shadow">
                  その他追加予定
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default About;
