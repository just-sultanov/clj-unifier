(ns example.i18n.dictionaries.ru)

(def dictionary
  {:example.api/unsupported "Команда `{1}` не поддерживается"

   :users/received          (fn [x]
                              (cond
                                (zero? x) "Список пользователей пуст"
                                (= 1 x) "Список из 1 пользователя успешно получен"
                                :else "Список из {1} пользователей успешно получен"))
   :user/received           "Пользователь `{1}` успешно получен"
   :user/exists             "Пользователь с указанным email `{1}` существует"
   :user/not-exists         "Пользователь с указанным email `{1}` не существует"
   :user/created            "Пользователь с указанным email `{1}` успешно создан"
   :user/deleted            "Пользователь с указанным email `{1}` успешно удален"})
