import http from "@/http";
export default {
  /**
   *
   */
  list(page, limit, tab, tag, search, success) {
    http.get(
      "qa/topic",
      {
        page: page,
        limit: limit,
        tab: tab,
        tag: tag,
        search: search
      },
      success
    );
  },
  /**
   *
   */
  detail(id, success) {
    http.get("qa/detail/" + id, success);
  }
};
