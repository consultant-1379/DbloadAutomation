package com.ericsson.db.repoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.ericsson.common.LogHandler;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

public class MetaTransferActionsProps {
	private RockFactory etlrepdb=null;
	 private  LogHandler log = null;
	 private Vector<Meta_collections> Meta_collecCache=null;
	
	private HashMap<Long,Vector<Meta_transfer_actions>> Meta_collecActionsCache=null;
	private String InterfaceName=null;
	private boolean Actionfound=false;
	public MetaTransferActionsProps(RockFactory etlrep,LogHandler log2,String InterfaceName) {
		etlrepdb=etlrep;
		log=log2;
		this.InterfaceName=InterfaceName;
		Meta_collecCache=new Vector<Meta_collections>();
		Meta_collecActionsCache=new HashMap<Long,Vector<Meta_transfer_actions>>();
		//System.out.println("Rockfactory rock constructor "+etlrepdb);
		populateMeta_sets_collections_Action();
	}
	
	private void populateMeta_sets_collections_Action() {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql=Queries._get_meta_collection_sets(InterfaceName,false);
		//System.out.println("sql  being executed :-"+sql);
		con=etlrepdb.getConnection();
		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				Long collection_set_id=rs.getLong(1);
				String version_number=rs.getNString(4);
				populategetMetaColletions(collection_set_id,version_number);
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			con=null;
			log.log(Level.WARNING, this.getClass().getName()+": failed to run the query get the meta_colletion_sets"+e.getMessage());
			
			
		}
		con=null;
		//return tables;
		
		
	}
	private void populategetMetaColletions(Long collection_set_id, String version_number) {
		Vector<Meta_collections> meta_collectionsvector=null;
		Meta_collections meta_collections=new Meta_collections(etlrepdb,false);
		meta_collections.setCollection_set_id(collection_set_id);
		meta_collections.setVersion_number(version_number);
		meta_collections.setEnabled_flag("Y");
		Meta_collections whereclause=meta_collections;
		Meta_collectionsFactory meta_collectionfactory;
		try {
			meta_collectionfactory = new Meta_collectionsFactory(etlrepdb,whereclause,false);
			Meta_collecCache = meta_collectionfactory.get();
			Iterator<Meta_collections> it=Meta_collecCache.iterator();
			while(it.hasNext()) {
				Pattern pattern=Pattern.compile("Adapter.*");
				Meta_collections meta_collections_tmp=it.next();
				Matcher matcher=pattern.matcher(meta_collections_tmp.getCollection_name());
				if(matcher.find()) {
					//System.out.println("Collection name "+meta_collections_tmp.getCollection_name());
					populateMetaAdapterActions(meta_collections_tmp.getCollection_id(), meta_collections_tmp.getCollection_set_id(), meta_collections_tmp.getVersion_number());
				}
			}
		} catch (SQLException | RockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void populateMetaAdapterActions(Long collection_id,Long collection_set_id,String VERSION_NUMBER) {
		Vector<Meta_transfer_actions> meta_transfer_actions_list=new Vector<Meta_transfer_actions>();
		Meta_transfer_actions meta_transfer_actions=new Meta_transfer_actions(etlrepdb,false);
		meta_transfer_actions.setCollection_id(collection_id);
		meta_transfer_actions.setCollection_set_id(collection_set_id);
		meta_transfer_actions.setVersion_number(VERSION_NUMBER);
		meta_transfer_actions.setAction_type("Parse");
		meta_transfer_actions.setEnabled_flag("Y");
		try {
			Meta_transfer_actionsFactory meta_transfer_actionsFactory=new  Meta_transfer_actionsFactory(etlrepdb,meta_transfer_actions,false);
			meta_transfer_actions_list = meta_transfer_actionsFactory.get();
			Meta_collecActionsCache.put(collection_id,meta_transfer_actions_list);
			Iterator<Meta_transfer_actions> it=meta_transfer_actions_list.iterator();
			while(it.hasNext()) {
				Actionfound=true;
				Meta_transfer_actions mta=it.next();
				//System.out.println("Action is "+mta.getAction_type());
			}		
		} catch (SQLException | RockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	 public boolean isActionfound() {
		return Actionfound;
	}

	public Vector<Meta_collections> getMeta_collecCache() {
			return Meta_collecCache;
		}
	 public HashMap<Long, Vector<Meta_transfer_actions>> getMeta_collecActionsCache() {
			return Meta_collecActionsCache;
		}

}
