class TvListResult {
  late List<TvData> tvList;

  void getResultFromMap(data) {
    tvList = [];
    data.forEach((info) {
      tvList.add(TvData()
        ..name = info["tvName"]
        ..uId = info["tvUID"]
        ..ipAddress = info["ipAddress"]);
    });
  }
}

class TvData {
  late String name;
  late String uId;
  late String ipAddress;
}
