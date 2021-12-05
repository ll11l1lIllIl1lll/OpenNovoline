package cc.novoline.gui.screen.alt.repository.hypixel;

import cc.novoline.utils.java.Checks;
import java.util.Date;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author xDelsy
 */
public class HypixelProfile {

    private static final HypixelProfile EMPTY = of("Default", 1, null, null, null, null, null, null, System.currentTimeMillis());

    /* fields */
    private final String rank;
    private final int level;
    private final String guild;
    private final int achievementPoints;
    private final int quests;
    private final int friends;
    private final Date firstLoginDate;
    private final Date lastLoginDate;
    private final Date cachedDate;

    private HypixelBan ban;

    /* constructors */
    //region Af long constructor
    public HypixelProfile(@Nullable String rank, int level, @Nullable String guild, int achievementPoints, int quests,
                          int friends, @Nullable Date firstLogin, @Nullable Date lastLogin, @Nullable Date cached) {
        this.rank = rank;
        this.level = level;
        this.guild = guild;
        this.achievementPoints = achievementPoints;
        this.quests = quests;
        this.friends = friends;
        this.firstLoginDate = firstLogin;
        this.lastLoginDate = lastLogin;
        this.cachedDate = cached;
    }
    //endregion

    static @NotNull HypixelProfile of(@Nullable String rank, int level, @Nullable String guild,
                             @Nullable Integer achievementPoints, @Nullable Integer quests, @Nullable Integer friends,
                             @Nullable Long firstLogin, @Nullable Long lastLogin, @Nullable Long cached) {
        if (achievementPoints != null) Checks.notNegative(achievementPoints, "achievement points");
        if (quests != null) Checks.notNegative(quests, "quests");
        if (friends != null) Checks.notNegative(friends, "friends");
        if (firstLogin != null) Checks.notNegative(firstLogin, "first login timestamp");
        if (lastLogin != null) Checks.notNegative(lastLogin, "last login timestamp");
        if (cached != null) Checks.notNegative(cached, "cached timestamp");

        rank = rank != null ? mapRank(rank.trim()) : "Default";
        level = Math.max(1, level);
        guild = guild != null ? guild.trim() : null;
        achievementPoints = achievementPoints == null ? 0 : achievementPoints;
        quests = quests == null ? 0 : quests;
        friends = friends == null ? 0 : friends;

        Date firstLoginDate = firstLogin != null ? new Date(firstLogin) : null, // @off
                lastLoginDate = lastLogin != null ? new Date(lastLogin) : null,
                cachedDate = cached != null ? new Date(cached) : null; // @on

        return new HypixelProfile(rank, level, guild, achievementPoints, quests, friends, firstLoginDate, lastLoginDate,
                cachedDate);
    }

    public static HypixelProfile empty() {
        return EMPTY;
    }

    /* methods */
    public void ban(@NotNull String reason, long date) {
        if (ban != null) throw new IllegalStateException("alt is already marked as banned!");
        this.ban = HypixelBan.of(reason, date);
    }

    public boolean isDefaultRank() {
        return StringUtils.isEmpty(rank) || rank.equalsIgnoreCase("default");
    }

    //region Lombok
    public int getLevel() {
        return level;
    }

    @Nullable
    public String getRank() {
        return rank;
    }

    public int getFriends() {
        return friends;
    }

    public int getAchievementPoints() {
        return achievementPoints;
    }

    @Nullable
    public Date getFirstLoginDate() {
        return firstLoginDate;
    }

    @Nullable
    public Date getLastLoginDate() {
        return lastLoginDate;
    }
    //endregion

    //region Unimportant shit
    @NotNull
    private static String mapRank(@NotNull String rank) {
        Checks.notBlank(rank, "rank");

        switch (rank.toLowerCase()) { // @off
            case "default":
                return "Default";
            case "vip":
                return "VIP";
            case "vip+":
            case "vip_plus":
                return "VIP+";
            case "mvp":
                return "MVP";
            case "mvp+":
            case "mvp_plus":
                return "MVP+";
            case "youtube":
                return "YouTube";
            case "helper":
                return "Helper";
            case "moderator":
            case "mod":
                return "Moderator";
            case "admin":
            case "adm":
                return "Admin";
            case "builder":
                return "Builder";
        } // @on

        return rank;
    }

    public @NotNull NBTBase asNBTCompound() {
        NBTTagCompound compound = new NBTTagCompound();

        if (rank != null && !rank.equalsIgnoreCase("default")) compound.setString("rank", rank);
        compound.setInteger("level", level);
        if (guild != null) compound.setString("guild", guild);
        if (achievementPoints > 0) compound.setInteger("achievement_points", achievementPoints);
        if (quests > 0) compound.setInteger("quests", quests);
        if (friends > 0) compound.setInteger("friends", friends);
        if (firstLoginDate != null) compound.setLong("first_login", firstLoginDate.getTime());
        if (lastLoginDate != null) compound.setLong("last_login", lastLoginDate.getTime());
        if (cachedDate != null) compound.setLong("cached", cachedDate.getTime());

        return compound;
    }

}
