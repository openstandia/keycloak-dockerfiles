package com.example;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ItemDisplayBean {

	public String name;
	public String uri;
	public String subject;
	public String ownerName;

	public boolean isOwner;
	public boolean viewable;
	public boolean updatable;
	public boolean deletable;

	public ItemDisplayBean() {
	}

	public ItemDisplayBean(String name, String uri) {
		this.name = name;
		this.uri = uri;
		this.subject = uri.replace(Item.URI_PREFIX, "");
	}

	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

	public boolean isViewable() {
		return viewable;
	}

	public void setViewable(boolean viewable) {
		this.viewable = viewable;
	}

	public boolean isUpdatable() {
		return updatable;
	}

	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

}
