package kr.guinnessgroup.serverLog.utils;

public enum Message {
    // Player
    PLAYER_JOIN("/Player/Join", "play.join"),
    PLAYER_QUIT("/Player/Quit", "play.leave"),
    PLAYER_KICK("/Player/Kick", "play.kick"),
    PLAYER_DEATH("/Player/Death", "play.death"),
    PLAYER_TELEPORT("/Player/Teleport", "play.teleport"),
    PLAYER_GAMEMODE("/Player/GameMode Change", "play.gamemode_change"),

    // Bucket
    BUCKET_EMPTY("/Player/Bucket Empty", "bucket.empty"),
    BUCKET_FILL("/Player/Bucket Fill", "bucket.fill"),
    BUCKET_ENTITY("/Player/Bucket Capture Entity", "bucket.entity"),

    // Item
    ITEM_EGG_SPAWN("/Item/Egg Spawn", "item.egg_spawn"),
    ITEM_DROP_ITEM("/Item/Drop Item", "item.drop_item"),
    ITEM_PICKUP_ITEM("/Item/Pickup Item", "item.pickup_item"),

    // Block
    BLOCK_BREAK("/Block/Block Break", "block.break"),
    BLOCK_PLACE("/Block/Block Place", "block.place"),

    // ETC
    CHAT("/Chat", "chat"),
    COMMAND("/Command", "command"),
    CHUNK_LOAD("/Chunk Load", "chunk"),
    ENTITY_COUNT("/Entity Count", "entity"),
    PLAYER_COUNT("/Player Count", "player"),
    ;

    private final String path;
    private final String langKey;

    Message(String path, String langKey) {
        this.path = path;
        this.langKey = langKey;
    }

    public String getPath() {
        return "/Logs" + path;
    }

    public String getLangKey() {
        return langKey;
    }
}