package cube.ware.data.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;
import com.common.utils.log.LogUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Room数据库迁移处理类
 *
 * @author LiuFeng
 * @data 2020/2/12 18:13
 */
public class CubeDBMigration extends Migration {
    private int startVersion;
    private int endVersion;

    /**
     * 私有构造方法，不让外部类创建实例
     *
     * @param startVersion
     * @param endVersion
     */
    private CubeDBMigration(int startVersion, int endVersion) {
        super(startVersion, endVersion);
        this.startVersion = startVersion;
        this.endVersion = endVersion;
    }

    /**
     * 数据迁移方法
     *
     * @param database
     */
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        LogUtil.i("migrate --> startVersion: " + startVersion + " endVersion: " + endVersion + " currentVersion: " + database.getVersion());

        int newVersion = endVersion;
        if (newVersion == 2) {
            // 举例
            //addField(CubeRecentSession.class, "faceUrl", String.class, database);
            //database.execSQL("ALTER TABLE CubeRecentSession ADD COLUMN faceUrl TEXT");
        }
        else if (newVersion == 3) {
            //database.execSQL("···");
        }
        else if (newVersion == 4) {
            //database.execSQL("···");
        }
    }

    /**
     * 获取创建全部数据库迁移
     *
     * @return
     */
    public static Migration[] getAllMigrations() {
        List<Migration> migrations = new ArrayList<>();
        int newVersion = CubeDBConfig.DB_VERSION;
        for (int i = 1; i < newVersion; i++) {
            migrations.add(new CubeDBMigration(i, i + 1));
        }

        return migrations.toArray(new Migration[0]);
    }

    /**
     * 添加域字段
     *
     * @param tableCls
     * @param fieldName
     * @param fieldCls
     * @param database
     */
    private void addField(Class<?> tableCls, String fieldName, Class<?> fieldCls, SupportSQLiteDatabase database) {
        String fieldType;
        if (fieldCls == String.class) {
            fieldType = "TEXT";
        }
        else if (fieldCls == int.class || fieldCls == long.class || fieldCls == boolean.class) {
            fieldType = "INTEGER";
        }
        else {
            fieldType = fieldCls.getSimpleName();
        }

        String table = tableCls.getSimpleName();
        String sql = "ALTER TABLE " + table + " ADD COLUMN " + fieldName + " " + fieldType;
        database.execSQL(sql);
    }
}
