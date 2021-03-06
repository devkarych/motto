package github.karchx.motto.models.storages

import github.karchx.motto.search_engine.citaty_info_website.items.Anime

class AnimeStorage(private val shuffle: Boolean) {

    fun getAnime(): ArrayList<Anime> {
        val anime = ArrayList<Anime>()

        anime.add(
            Anime(
                "ataka_titanov",
                "Атака титанов",
                "/anime/ataka-titanov-vtorzhenie-titanov-shingeki-no-kyojin"
            )
        )
        anime.add(
            Anime(
                "kimetsu",
                "Клинок, рассекающий демонов",
                "/anime/klinok-rassekayushii-demonov-kimetsu-no-yaiba"
            )
        )
        anime.add(
            Anime(
                "gurren_lagann",
                "Гуррен Лаганн",
                "/anime/gurren-lagann-proch-iz-etogo-mira-tengen-toppa-gurren-lagann"
            )
        )
        anime.add(
            Anime(
                "vanpanchmen",
                "Ванпанчмен",
                "/anime/vanpanchmen-one-punch-man"
            )
        )
        anime.add(
            Anime(
                "bebop",
                "Ковбой Бибоп",
                "/anime/kovboi-bibop-cowboy-bebop"
            )
        )
        anime.add(Anime("bezdomnyi_bog", "Бездомный Бог", "/anime/bezdomnyi-bog-noragami"))
        anime.add(Anime("berserk_kenpuu", "Берсерк", "/anime/berserk-kenpuu-denki-berserk"))
        anime.add(Anime("blich_bleach", "Блич", "/anime/blich-bleach"))
        anime.add(
            Anime(
                "stray_dogs",
                "Бродячие псы",
                "/anime/brodyachie-psy-literaturnye-genii-bungou-stray-dogs"
            )
        )
        anime.add(Anime("one_piece", "Ван Пис", "/anime/van-pis-one-piece"))
        anime.add(Anime("voleibol_haikyuu", "Волейбол!!", "/anime/voleibol-haikyuu"))
        anime.add(Anime("gintama", "Гинтама", "/anime/gintama-gintama"))
        anime.add(
            Anime(
                "evangelion_neon",
                "Евангелион",
                "/anime/evangelion-neon-genesis-evangelion"
            )
        )
        anime.add(
            Anime(
                "kod_gias",
                "Код Гиас: Восставший Лелуш",
                "/anime/kod-gias-vosstavshii-lelush-code-geass-lelouch-of-the-rebellion"
            )
        )
        anime.add(
            Anime(
                "boku_no_hero_academia",
                "Моя геройская академия",
                "/anime/moya-geroiskaya-akademiya-boku-no-hero-academia"
            )
        )
        anime.add(Anime("naruto", "Наруто", "/anime/naruto-naruto"))
        anime.add(
            Anime(
                "jojos_bizarre_adventure",
                "Невероятные приключения ДжоДжо",
                "/anime/neveroyatnye-priklyucheniya-dzhodzho-jojos-bizarre-adventure"
            )
        )
        anime.add(
            Anime(
                "boku_dake_ga",
                "Город, в котором меня нет",
                "/anime/gorod-v-kotorom-menya-net-boku-dake-ga-inai-machi"
            )
        )
        anime.add(
            Anime(
                "hunter_x_hunter",
                "Охотник × Охотник",
                "/anime/ohotnik-x-ohotnik-hunter-x-hunter"
            )
        )
        anime.add(
            Anime(
                "oregairu",
                "Розовая пора моей школьной жизни",
                "/anime/rozovaya-pora-moei-shkolnoi-zhizni-sploshnoi-obman-oregairu"
            )
        )
        anime.add(
            Anime(
                "fairy_tail",
                "Сказка о хвосте феи",
                "/anime/skazka-o-hvoste-fei-fairy-tail"
            )
        )
        anime.add(
            Anime(
                "fullmetal_alchemist",
                "Стальной алхимик",
                "/anime/stalnoi-alhimik-fullmetal-alchemist"
            )
        )
        anime.add(Anime("kimi_no_na_wa", "Твое имя", "/anime/tvoe-imya-kimi-no-na-wa"))
        anime.add(Anime("kuroshitsuji", "Тёмный дворецкий", "/anime/temnyi-dvoreckii-kuroshitsuji"))
        anime.add(Anime("death_note", "Тетрадь смерти", "/anime/tetrad-smerti-death-note"))
        anime.add(
            Anime(
                "tokyo_ghoul",
                "Токийский гуль",
                "/anime/tokiiskii-gul-tokiiskii-monstr-tokyo-ghoul"
            )
        )
        anime.add(Anime("hacksign", ".хак//ЗНАК", "/anime/hakznak-hacksign"))
        anime.add(Anime("samurai_7", "7 самураев", "/anime/7-samuraev-7-samurai"))

        if (shuffle) {
            anime.shuffle()
            return anime
        }
        return anime
    }
}
