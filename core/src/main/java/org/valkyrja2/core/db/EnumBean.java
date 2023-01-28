package org.valkyrja2.core.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 枚举对象bean
 *
 * @author Tequila
 * @create 2022/11/15 11:59
 **/
public class EnumBean {

	public static final String FIELD_CODE = "code";
	public static final String FIELD_DISPLAY = "display";

	@NotEmpty(message = "类型字段不能为空")
	private final String code;
	private final String display;

	@JsonCreator
	private EnumBean(@JsonProperty(FIELD_CODE) String code, @JsonProperty(FIELD_DISPLAY) String display) {
		this.code = code;
		this.display = display;
	}
	
	public static EnumBean of(Map<?, ?> value) {
		if (value.containsKey(FIELD_CODE)) {
			return new EnumBean(
					value.get(FIELD_CODE).toString(),
					"");
		}
		return null;
	}

	public String getCode() {
		return code;
	}

	public String getDisplay() {
		return display;
	}

}
